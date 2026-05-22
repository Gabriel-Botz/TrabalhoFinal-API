package org.serratec.TrabalhoFinal_API.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.serratec.TrabalhoFinal_API.dto.request.ListaFavoritosRequestDTO;
import org.serratec.TrabalhoFinal_API.dto.response.ListaFavoritosResponseDTO;
import org.serratec.TrabalhoFinal_API.services.ListaFavoritosService;
import org.springframework.beans.factory.annotation.Autowired;
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
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/lista-favoritos")
public class ListaFavoritosController {

    @Autowired
    private ListaFavoritosService listaFavoritosService;

    @Operation(summary = "Listar todas as listas de favoritos", description = "Retorna uma lista de todas as listas de favoritos criadas pelos usuários.")
    @ApiResponse(responseCode = "200", description = "Lista de favoritos retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ListaFavoritosResponseDTO>> listarListaFavoritos() {
        List<ListaFavoritosResponseDTO> listaFavoritos = listaFavoritosService.listar();
        return ResponseEntity.ok(listaFavoritos);
    }

    @Operation(summary = "Buscar lista de favoritos por ID", description = "Retorna uma lista de favoritos específica com base no ID fornecido.")
    @ApiResponse(responseCode = "200", description = "Lista de favoritos retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Lista de favoritos não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<ListaFavoritosResponseDTO> buscarListaFavoritosPorId(@PathVariable UUID id) {
        ListaFavoritosResponseDTO listaFavoritos = listaFavoritosService.buscarPorId(id);
        if (listaFavoritos != null) {
            return ResponseEntity.ok(listaFavoritos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Criar nova lista de favoritos", description = "Cria uma nova lista de favoritos com os dados fornecidos.")
    @ApiResponse(responseCode = "201", description = "Lista de favoritos criada com sucesso")
    @PostMapping
    public ResponseEntity<ListaFavoritosResponseDTO> criarListaFavoritos(
            @RequestBody ListaFavoritosRequestDTO listaFavoritosRequestDTO) {
        ListaFavoritosResponseDTO novaLista = listaFavoritosService.criar(listaFavoritosRequestDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novaLista.getId()).toUri();

        return ResponseEntity.created(location).body(novaLista);
    }

    @Operation(summary = "Atualizar lista de favoritos", description = "Atualiza uma lista de favoritos existente com os dados fornecidos.")
    @ApiResponse(responseCode = "200", description = "Lista de favoritos atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Lista de favoritos não encontrada")
    @PutMapping("/{id}")
    public ResponseEntity<ListaFavoritosResponseDTO> atualizarListaFavoritos(
            @PathVariable UUID id, @RequestBody ListaFavoritosRequestDTO listaFavoritosRequestDTO) {
        ListaFavoritosResponseDTO listaAtualizada = listaFavoritosService.atualizar(id, listaFavoritosRequestDTO);
        if (listaAtualizada != null) {
            return ResponseEntity.ok(listaAtualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deletar lista de favoritos", description = "Deleta uma lista de favoritos existente com base no ID fornecido.")
    @ApiResponse(responseCode = "204", description = "Lista de favoritos deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Lista de favoritos não encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarListaFavoritos(@PathVariable UUID id) {
        boolean deletado = listaFavoritosService.deletarListaFavoritos(id);
        if (deletado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
