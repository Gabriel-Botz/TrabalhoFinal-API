package org.serratec.trabalho_final_api.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.dto.request.UsuarioRequestDTO;
import org.serratec.trabalho_final_api.dto.response.UsuarioResponseDTO;
import org.serratec.trabalho_final_api.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Usuarios", description = "Endpoints para gerenciamento de Usuarios")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

        @Autowired
        private UsuarioService service;

        /* Métodos GETs */

        @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista contendo todos os usuarios cadastrados no sistema.")
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
        @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
        public List<UsuarioResponseDTO> listar() {
                return service.listarTodos();
        }

        @Operation(summary = "Buscar animal por ID", description = "Retorna os detalhes completos de um animal específico com base no ID informado.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Animal encontrado com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Nenhum animal encontrado com o ID informado")
        })
        @GetMapping("/id")
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        public UsuarioResponseDTO buscar(
                        @Parameter(description = "ID único do usuario", example = "1") @Valid @RequestParam UUID id) { // trows
                                                                                                                       // Exception
                return service.buscar(id);
        }

        /* Métodos POSTs */

        @Operation(summary = "Cadastrar novo usuario", description = "Salva um novo usuario no banco de dados e retorna o recurso criado acompanhado da sua URI de localização.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Usuario cadastrado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou mal formatados")
        })
        @PostMapping
        @PreAuthorize("permitAll()")
        public ResponseEntity<UsuarioResponseDTO> salvar(@Valid @RequestBody UsuarioRequestDTO request) {
                UsuarioResponseDTO response = service.salvar(request);

                URI uri = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(response.id())
                                .toUri();

                return ResponseEntity.created(uri).body(response);
        }

        @Operation(summary = "Cadastrar múltiplos usuarios", description = "Recebe uma lista de usuarios em lote e realiza o cadastro em massa de uma única vez.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Lista de usuarios processada e cadastrada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Um ou mais elementos da lista contêm dados inválidos")
        })
        @PostMapping("/salvar-lista")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<UsuarioResponseDTO>> salvarVarios(
                        @Valid @RequestBody List<UsuarioRequestDTO> request) {
                List<UsuarioResponseDTO> response = service.salvarList(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        /* Método PUTs */

        @Operation(summary = "Alterar todas as propriedades do usuario", description = "Substitui completamente os dados de um usuario existente com base no ID informado.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Corpo da requisição possui dados inconsistentes"),
                        @ApiResponse(responseCode = "404", description = "Usuario não localizado para atualização")
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        public UsuarioResponseDTO atualizar(
                        @Parameter(description = "ID do animal a ser modificado", example = "1") @Valid @PathVariable UUID id,
                        @Valid @RequestBody UsuarioRequestDTO request) {// throws RecursoNaoEncontradoException

                return service.atualizar(id, request);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        public ResponseEntity<Void> deletar(
                        @Parameter(description = "ID do usuario a ser deletado", example = "1") @PathVariable UUID id) { // throws
                                                                                                                         // RecursoNaoEncontradoException
                service.excluir(id);
                return ResponseEntity.noContent().build();
        }
}
