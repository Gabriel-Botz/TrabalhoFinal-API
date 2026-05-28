package org.serratec.trabalho_final_api.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.dto.request.UsuarioRequestDTO;
import org.serratec.trabalho_final_api.dto.response.UsuarioResponseDTO;
import org.serratec.trabalho_final_api.exception.AcessoNegadoException;
import org.serratec.trabalho_final_api.exception.ErroResposta;
import org.serratec.trabalho_final_api.exception.RecursoJaExistenteException;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Usuários", description = "Endpoints para gerenciamento e cadastro de Usuários")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

      @Autowired
      private UsuarioService service;

      /* --> Métodos GETs */
      @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista contendo todos os usuários cadastrados no sistema. **Acesso restrito: ROLE_ADMIN.**")
      @ApiResponses(value = {
                  @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
                  @ApiResponse(responseCode = "401", description = "Não autenticado - Token JWT ausente ou inválido"),
                  @ApiResponse(responseCode = "403", description = "Acesso negado - Operação exclusiva para administradores", content = @Content(schema = @Schema(implementation = ErroResposta.class)))
      })
      @GetMapping
      public ResponseEntity<List<UsuarioResponseDTO>> listar() {
            return ResponseEntity.ok(service.listarTodos());
      }

      @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes completos de um usuário específico com base no UUID informado.")
      @ApiResponses(value = {
                  @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
                  @ApiResponse(responseCode = "401", description = "Não autenticado"),
                  @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado com o ID informado", content = @Content(schema = @Schema(implementation = ErroResposta.class)))
      })
      @GetMapping("/{id}")
      public ResponseEntity<UsuarioResponseDTO> buscar(
                  @Parameter(description = "ID único do usuário (formato UUID)", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable UUID id)
                  throws RecursoNaoEncontradoException {
            return ResponseEntity.ok(service.buscar(id));
      }

      @Operation(summary = "Buscar usuários por término do username", description = "Retorna uma lista de usuários cujo username termina com o termo pesquisado (Busca Case-Insensitive).")
      @ApiResponses(value = {
                  @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
                  @ApiResponse(responseCode = "401", description = "Não autenticado")
      })
      @GetMapping("/username/{username}")
      public ResponseEntity<List<UsuarioResponseDTO>> buscarUsername(
                  @Parameter(description = "Termo ou sufixo do username", example = "Phon") @PathVariable String username)
                  throws RecursoNaoEncontradoException {
            return ResponseEntity.ok(service.buscarPorUsername(username));
      }

      @Operation(summary = "Buscar usuários por Tipo (Role)", description = "Retorna todos os usuários filtrados pelo tipo de perfil: USER ou ADMIN.")
      @ApiResponses(value = {
                  @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
                  @ApiResponse(responseCode = "400", description = "Tipo de usuário inválido ou mal formatado no path", content = @Content(schema = @Schema(implementation = ErroResposta.class))),
                  @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = AcessoNegadoException.class)))
      })
      @GetMapping("/tipo/{tipoUsuario}")
      public ResponseEntity<List<UsuarioResponseDTO>> buscarPorTipoUsuario(
                  @Parameter(description = "Tipo do usuário (USER ou ADMIN)", example = "USER") @PathVariable String tipoUsuario)
                  throws RecursoNaoEncontradoException {
            return ResponseEntity.ok(service.buscarPorTipoUsuario(tipoUsuario));
      }

      /* --> Métodos POSTs */
      @Operation(summary = "Cadastrar novo usuário", description = "Salva um novo usuário no banco de dados, criptografa a senha e envia e-mail de confirmação. **Acesso Público.**")
      @ApiResponses(value = {
                  @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
                  @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos (Erros de validação ou JSON mal formatado)", content = @Content(schema = @Schema(implementation = ErroResposta.class))),
                  @ApiResponse(responseCode = "409", description = "Conflito - Username ou E-mail duplicados no banco de dados", content = @Content(schema = @Schema(implementation = RecursoJaExistenteException.class)))
      })
      @PostMapping
      public ResponseEntity<UsuarioResponseDTO> salvar(@Valid @RequestBody UsuarioRequestDTO request) {
            UsuarioResponseDTO response = service.salvar(request);

            URI uri = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.id())
                        .toUri();

            return ResponseEntity.created(uri).body(response);
      }

      @Operation(summary = "Cadastrar múltiplos usuários (Em lote)", description = "Recebe uma lista de novos usuários e realiza o cadastro em massa de uma só vez. **Acesso restrito: ROLE_ADMIN.**")
      @ApiResponses(value = {
                  @ApiResponse(responseCode = "201", description = "Lista de usuários processada e cadastrada com sucesso"),
                  @ApiResponse(responseCode = "400", description = "Um ou mais elementos da lista contêm dados inválidos ou nulos", content = @Content(schema = @Schema(implementation = ErroResposta.class))),
                  @ApiResponse(responseCode = "401", description = "Não autenticado"),
                  @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = AcessoNegadoException.class))),
                  @ApiResponse(responseCode = "409", description = "Conflito de integridade encontrado na lista", content = @Content(schema = @Schema(implementation = RecursoJaExistenteException.class)))
      })
      @PostMapping("/salvar-lista")
      public ResponseEntity<List<UsuarioResponseDTO>> salvarVarios(
                  @Valid @RequestBody List<UsuarioRequestDTO> request) {
            List<UsuarioResponseDTO> response = service.salvarList(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
      }

      /* --> Método PUTs */
      @Operation(summary = "Atualizar propriedades do usuário", description = "Substitui de forma dinâmica as informações de um usuário existente com base no ID informado. **Acesso restrito: ROLE_ADMIN.**")
      @ApiResponses(value = {
                  @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
                  @ApiResponse(responseCode = "400", description = "Corpo da requisição possui dados inválidos", content = @Content(schema = @Schema(implementation = ErroResposta.class))),
                  @ApiResponse(responseCode = "401", description = "Não autenticado"),
                  @ApiResponse(responseCode = "403", description = "Acesso negado"),
                  @ApiResponse(responseCode = "404", description = "Usuário não localizado para atualização", content = @Content(schema = @Schema(implementation = ErroResposta.class))),
                  @ApiResponse(responseCode = "409", description = "Conflito - Nova alteração gera duplicidade de registros", content = @Content(schema = @Schema(implementation = ErroResposta.class)))
      })
      @PutMapping("/{id}")
      public ResponseEntity<UsuarioResponseDTO> atualizar(
                  @Parameter(description = "ID do usuário a ser modificado (formato UUID)", example = "123e4567-e89b-12d3-a456-426614174000") @Valid @PathVariable UUID id,
                  @Valid @RequestBody UsuarioRequestDTO request) throws RecursoNaoEncontradoException {

            return ResponseEntity.ok(service.atualizar(id, request));
      }

      @Operation(summary = "Deletar um usuário", description = "Remove de forma definitiva o usuário do sistema através do ID e envia e-mail de encerramento. **Acesso restrito: ROLE_ADMIN.**")
      @ApiResponses(value = {
                  @ApiResponse(responseCode = "244", description = "Usuário excluído com sucesso (No Content)"),
                  @ApiResponse(responseCode = "401", description = "Não autenticado"),
                  @ApiResponse(responseCode = "403", description = "Acesso negado"),
                  @ApiResponse(responseCode = "404", description = "Usuário não localizado no sistema", content = @Content(schema = @Schema(implementation = ErroResposta.class))),
                  @ApiResponse(responseCode = "409", description = "Não é possível excluir o usuário porque ele possui vínculos pendentes no sistema", content = @Content(schema = @Schema(implementation = ErroResposta.class)))
      })
      @DeleteMapping("/{id}")
      public ResponseEntity<Void> deletar(
                  @Parameter(description = "ID do usuário a ser deletado (formato UUID)", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable UUID id)
                  throws RecursoNaoEncontradoException {

            service.excluir(id);
            return ResponseEntity.noContent().build();
      }

}
