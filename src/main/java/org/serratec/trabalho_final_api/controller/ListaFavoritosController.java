package org.serratec.trabalho_final_api.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.dto.request.ListaFavoritosRequestDTO;
import org.serratec.trabalho_final_api.dto.response.ListaFavoritosResponseDTO;
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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/lista-favoritos")
@Tag(name = "Lista de favoritos", description = "Endpoints para gerenciamento das listas de favoritos dos usuários")
public class ListaFavoritosController {

    @Autowired
    private ListaFavoritosService listaFavoritosService;


    // Listas públicas
    @Operation(
        summary = "Lista todas as listas de favoritos públicas", 
        description = "Retorna uma lista de todas as listas públicas de favoritos criadas pelos usuários.")
    @ApiResponse(
        responseCode = "200",
        description = "Lista de favoritos retornada com sucesso")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
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
    @ApiResponse(
        responseCode = "404", 
        description = "Listas de favoritos privadas não encontradas para o usuário")
    @ApiResponse(
        responseCode = "403", 
        description = "Acesso negado. O usuário não tem permissão para acessar as listas privadas de outros usuários")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{idLista}")
    public ResponseEntity<ListaFavoritosResponseDTO> buscarListaFavoritosPorId(@PathVariable UUID idLista) {
        ListaFavoritosResponseDTO listaFavoritos = listaFavoritosService.buscarPorId(idLista);
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
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
            Authentication authentication) {

        ListaFavoritosResponseDTO novaLista = listaFavoritosService.criar(username, listaFavoritosRequestDTO);;

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novaLista.getId()).toUri();

        return ResponseEntity.created(location).body(novaLista);
    }


    // Atualizar lista de favoritos
    @Operation(
        summary = "Atualizar lista de favoritos", 
        description = "Atualiza uma lista de favoritos existente com os dados fornecidos.")
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de favoritos atualizada com sucesso")
    @ApiResponse(
        responseCode = "404", 
        description = "Lista de favoritos não encontrada")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("{username}/{idLista}")
    public ResponseEntity<ListaFavoritosResponseDTO> atualizarListaFavoritos(
            @PathVariable String username,
            @PathVariable UUID idLista, 
            @RequestBody ListaFavoritosRequestDTO listaFavoritosRequestDTO,
            Authentication authentication) {
        String usuario = authentication.getName();
        ListaFavoritosResponseDTO listaAtualizada = listaFavoritosService.atualizar(idLista, listaFavoritosRequestDTO,
                usuario);
        if (listaAtualizada != null) {
            return ResponseEntity.ok(listaAtualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Deletar lista de favoritos
    @Operation(
        summary = "Deletar lista de favoritos", 
        description = "Deleta uma lista de favoritos existente com base no ID fornecido.")
    @ApiResponse(
        responseCode = "204", 
        description = "Lista de favoritos deletada com sucesso")
    @ApiResponse(
        responseCode = "404", 
        description = "Lista de favoritos não encontrada")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{username}/{idLista}")
    public ResponseEntity<Void> deletarListaFavoritos(
        @PathVariable String username, 
        @PathVariable UUID idLista) {

        listaFavoritosService.deletar(username, idLista);
        return ResponseEntity.noContent().build();
    }


    // Adicionar um filme à lista de favoritos
    @Operation(
        summary = "Adicionar filme à lista de favoritos", 
        description = "Adiciona um filme específico a uma lista de favoritos existente.")
    @ApiResponse(
        responseCode = "200",
        description = "Filme adicionado à lista de favoritos com sucesso")
    @ApiResponse(
        responseCode = "404",
        description = "Lista de favoritos ou filme não encontrado")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/filmes/{username}")
    public ResponseEntity<ListaFavoritosResponseDTO> adicionarFilme(
        @PathVariable String username, 
        @RequestParam UUID idLista, 
        @RequestParam UUID idFilme) {
            
            ListaFavoritosResponseDTO lista = listaFavoritosService.adicionarFilme(username, idLista, idFilme);;

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(lista.getId()).toUri();

        return ResponseEntity.created(location).body(lista);
        }


    // Adicionar uma série à lista
    @Operation(
        summary = "Adicionar série à lista de favoritos", 
        description = "Adiciona uma série específica à uma lista de favoritos existente.")
    @ApiResponse(
        responseCode = "200",
        description = "Série adicionada à lista de favoritos com sucesso")
    @ApiResponse(
        responseCode = "404",
        description = "Lista de favoritos ou série não encontrada")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/series/{username}")
    public ResponseEntity<ListaFavoritosResponseDTO> adicionarSerie(
        @PathVariable String username, 
        @RequestParam UUID idLista,  
        @RequestParam UUID idSerie) {
            
            ListaFavoritosResponseDTO lista = listaFavoritosService.adicionarSerie(username, idLista, idSerie);;

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(lista.getId()).toUri();

        return ResponseEntity.created(location).body(lista);
        }


    // Deleta um filme da lista de favoritos
    @Operation(
        summary = "Deletar filme da lista de favoritos", 
        description = "Deleta um filme específico de uma lista de favoritos existente.")
    @ApiResponse(
        responseCode = "204",
        description = "Filme deletado da lista de favoritos com sucesso")
    @ApiResponse(
        responseCode = "404",
        description = "Lista de favoritos ou filme não encontrado")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/filmes/{username}")
    public ResponseEntity<ListaFavoritosResponseDTO> removerFilme(
        @PathVariable String username, 
        @RequestParam UUID idLista, 
        @RequestParam UUID idFilme) {
            
            ListaFavoritosResponseDTO listaDTO = listaFavoritosService.removerFilme(username, idLista, idFilme);

            return ResponseEntity.ok(listaDTO);
        }


    // Deleta uma série da lista de favoritos
    @Operation(
        summary = "Deletar série da lista de favoritos", 
        description = "Deleta uma série específica de uma lista de favoritos existente.")
    @ApiResponse(
        responseCode = "204",
        description = "Série deletada da lista de favoritos com sucesso")
    @ApiResponse(
        responseCode = "404",
        description = "Lista de favoritos ou série não encontrado")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/series/{username}")
    public ResponseEntity<ListaFavoritosResponseDTO> removerSerie(
        @PathVariable String username, 
        @RequestParam UUID idLista, 
        @RequestParam UUID idSerie) {
            
            ListaFavoritosResponseDTO listaDTO = listaFavoritosService.removerSerie(username, idLista, idSerie);

            return ResponseEntity.ok(listaDTO);
        }

}
