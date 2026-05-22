package org.serratec.TrabalhoFinal_API.controller;

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

@RestController
@RequestMapping("/lista-favoritos")
public class ListaFavoritosController {

    @Autowired
    private ListaFavoritosService listaFavoritosService;

    @GetMapping
    public ResponseEntity<List<ListaFavoritosResponseDTO>> listarListaFavoritos() {
        List<ListaFavoritosResponseDTO> listaFavoritos = listaFavoritosService.listarListaFavoritos();
        return ResponseEntity.ok(listaFavoritos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListaFavoritosResponseDTO> buscarListaFavoritosPorId(@PathVariable UUID id) {
        ListaFavoritosResponseDTO listaFavoritos = listaFavoritosService.buscarListaFavoritosPorId(id);
        if(listaFavoritos != null) {
            return ResponseEntity.ok(listaFavoritos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ListaFavoritosResponseDTO> criarListaFavoritos(@RequestBody ListaFavoritosRequestDTO listaFavoritosRequestDTO) {
        ListaFavoritosResponseDTO novaLista = listaFavoritosService.criarListaFavoritos(listaFavoritosRequestDTO);
        return ResponseEntity.ok(novaLista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListaFavoritosResponseDTO> atualizarListaFavoritos(@PathVariable UUID id, @RequestBody ListaFavoritosRequestDTO listaFavoritosRequestDTO) {
        ListaFavoritosResponseDTO listaAtualizada = listaFavoritosService.atualizarListaFavoritos(id, listaFavoritosRequestDTO);
        if(listaAtualizada != null) {
            return ResponseEntity.ok(listaAtualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarListaFavoritos(@PathVariable UUID id) {
        boolean deletado = listaFavoritosService.deletarListaFavoritos(id);
        if(deletado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
