package org.serratec.trabalho_final_api.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.dto.request.ListaFavoritosRequestDTO;
import org.serratec.trabalho_final_api.dto.response.ListaFavoritosResponseDTO;
import org.serratec.trabalho_final_api.dto.response.UsuarioResponseDTO;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.serratec.trabalho_final_api.services.ListaFavoritosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/lista-favoritos")
public class ListaFavoritosController {

    @Autowired
    private ListaFavoritosService listaFavoritosService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Listas públicas
    @Operation(
        summary = "Lista todas as listas de favoritos públicas", 
        description = "Retorna uma lista de todas as listas públicas de favoritos criadas pelos usuários.")
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de favoritos retornada com sucesso")
    @GetMapping("/publicas")
    public ResponseEntity<List<ListaFavoritosResponseDTO>> listarPublicas() {
        List<ListaFavoritosResponseDTO> listaFavoritos = listaFavoritosService.listarPublicas();
        return ResponseEntity.ok(listaFavoritos);
    }


    //Listas privadas
    @Operation(
        summary = "Lista listas de favoritos privadas", 
        description = "Retorna uma lista de todas as listas de favoritos privadas do usuario.")
    @ApiResponse(
        responseCode = "200",
        description = "Lista de favoritos retornada com sucesso")
    @GetMapping("/privadas/{username}")
    public ResponseEntity<List<ListaFavoritosResponseDTO>> listarPrivadas(@PathVariable String username) {
        List<ListaFavoritosResponseDTO> listaFavoritos = listaFavoritosService.listarPrivadas(username);
        return ResponseEntity.ok(listaFavoritos);
    }


    // Buscar por ID
    @Operation(
        summary = "Buscar lista de favoritos por ID", 
        description = "Retorna uma lista de favoritos específica com base no ID fornecido.")
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de favoritos retornada com sucesso")
    @ApiResponse(
        responseCode = "404", 
        description = "Lista de favoritos não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<ListaFavoritosResponseDTO> buscarListaFavoritosPorId(@PathVariable UUID id) {
        ListaFavoritosResponseDTO listaFavoritos = listaFavoritosService.buscarPorId(id);
        if (listaFavoritos != null) {
            return ResponseEntity.ok(listaFavoritos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Buscar por nome
    @Operation(
        summary = "Buscar lista de favoritos pelo nome", 
        description = "Retorna uma lista de favoritos específica com base no texto fornecido.")
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de favoritos retornada com sucesso")
    @ApiResponse(
        responseCode = "404", 
        description = "Lista de favoritos não encontrada")
    @GetMapping("/busca/?nome={nome}")
    public ResponseEntity<List<ListaFavoritosResponseDTO>> buscarListaFavoritosPorNome(@RequestParam String nome) {
        
        List<ListaFavoritosResponseDTO> listas = listaFavoritosService.buscarPorNomePublicas(nome);
        return ResponseEntity.ok(listas);

    }


    // Criar nova lista de favoritos
    @Operation(
        summary = "Criar nova lista de favoritos", 
        description = "Cria uma nova lista de favoritos com os dados fornecidos.")
    @ApiResponse(
        responseCode = "201", 
        description = "Lista de favoritos criada com sucesso")
    @PostMapping("/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ListaFavoritosResponseDTO> criarLista(
            @PathVariable String username,
            @RequestBody ListaFavoritosRequestDTO listaFavoritosRequestDTO, 
            Authentication authentication) { // <-Aqui

        ListaFavoritosResponseDTO novaLista = listaFavoritosService.criar(username, listaFavoritosRequestDTO);;

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novaLista.getId()).toUri();

        return ResponseEntity.created(location).body(novaLista);
    }




    @Operation(
        summary = "Atualizar lista de favoritos", 
        description = "Atualiza uma lista de favoritos existente com os dados fornecidos.")
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de favoritos atualizada com sucesso")
    @ApiResponse(
        responseCode = "404", 
        description = "Lista de favoritos não encontrada")
    @PutMapping("{username}/{id}")
    public ResponseEntity<ListaFavoritosResponseDTO> atualizarListaFavoritos(
            @PathVariable String username,
            @PathVariable UUID id, 
            @RequestBody ListaFavoritosRequestDTO listaFavoritosRequestDTO,
            Authentication authentication) {
        String usuario = authentication.getName();
        ListaFavoritosResponseDTO listaAtualizada = listaFavoritosService.atualizar(id, listaFavoritosRequestDTO,
                usuario);// <- Aqui
        if (listaAtualizada != null) {
            return ResponseEntity.ok(listaAtualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Deletar lista de favoritos", 
        description = "Deleta uma lista de favoritos existente com base no ID fornecido.")
    @ApiResponse(
        responseCode = "204", 
        description = "Lista de favoritos deletada com sucesso")
    @ApiResponse(
        responseCode = "404", 
        description = "Lista de favoritos não encontrada")
    @DeleteMapping("/{username}/{id}")
    public ResponseEntity<Void> deletarListaFavoritos(@PathVariable String username, @PathVariable UUID id) {

        listaFavoritosService.deletar(username, id);
        return ResponseEntity.noContent().build();

    }
}
