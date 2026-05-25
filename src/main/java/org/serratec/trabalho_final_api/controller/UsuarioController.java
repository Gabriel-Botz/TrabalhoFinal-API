package org.serratec.trabalho_final_api.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.dto.request.UsuarioRequestDTO;
import org.serratec.trabalho_final_api.dto.response.UsuarioResponseDTO;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
        private UsuarioService usuarioService;

        /* Métodos GETs */

        @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista contendo todos os usuários cadastrados no sistema.")
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
        @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<UsuarioResponseDTO>> listar() {
                return ResponseEntity.ok(usuarioService.listarTodos());
        }

        @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes completos de um usuário específico com base no ID informado.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado com o ID informado")
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        public ResponseEntity<UsuarioResponseDTO> buscar(
                        @Parameter(description = "ID único do usuário", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id)
                        throws RecursoNaoEncontradoException {
                return ResponseEntity.ok(usuarioService.buscar(id));
        }

        /* Métodos POSTs */

        @Operation(summary = "Cadastrar novo usuário com foto", description = "Salva um novo usuário e sua foto de perfil no banco de dados.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou mal formatados")
        })
        @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
        public ResponseEntity<UsuarioResponseDTO> salvar(
                        @Valid @RequestPart("usuario") UsuarioRequestDTO request,
                        @RequestPart("file") MultipartFile file) throws IOException {

                UsuarioResponseDTO response = usuarioService.salvar(request, file);

                URI uri = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(response.id())
                                .toUri();

                return ResponseEntity.created(uri).body(response);
        }

        @Operation(summary = "Cadastrar múltiplos usuários", description = "Recebe uma lista de usuários em lote e realiza o cadastro em massa de uma única vez.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Lista de usuários processada e cadastrada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Um ou mais elementos da lista contêm dados inválidos")
        })
        @PostMapping("/salvar-lista")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<UsuarioResponseDTO>> salvarVarios(
                        @Valid @RequestBody List<UsuarioRequestDTO> request) {
                List<UsuarioResponseDTO> response = usuarioService.salvarList(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        /* Método PUTs */

        @Operation(summary = "Alterar propriedades do usuário", description = "Atualiza os dados de um usuário existente e permite alterar a foto de perfil.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Corpo da requisição possui dados inconsistentes"),
                        @ApiResponse(responseCode = "404", description = "Usuário não localizado para atualização")
        })
        @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        public ResponseEntity<UsuarioResponseDTO> atualizar(
                        @Parameter(description = "ID do usuário a ser modificado", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id,
                        @Valid @RequestPart("usuario") UsuarioRequestDTO request,
                        @RequestPart(value = "file", required = false) MultipartFile file)
                        throws IOException {

                UsuarioResponseDTO response = usuarioService.atualizar(id, request, file);
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        public ResponseEntity<Void> deletar(
                        @Parameter(description = "ID do usuário a ser deletado", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id)
                        throws RecursoNaoEncontradoException {

                usuarioService.excluir(id);
                return ResponseEntity.noContent().build();
        }
}