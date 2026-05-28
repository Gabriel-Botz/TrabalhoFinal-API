package org.serratec.trabalho_final_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.serratec.trabalho_final_api.dto.request.FilmeRequestDTO;
import org.serratec.trabalho_final_api.dto.response.FilmeResponseDTO;
import org.serratec.trabalho_final_api.dto.response.TmdbCreditosResponseDTO;
import org.serratec.trabalho_final_api.dto.response.TmdbFilmeDetalhesDTO;
import org.serratec.trabalho_final_api.services.FilmeService;
import org.serratec.trabalho_final_api.services.TmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/filmes")
@Tag(name = "Filmes", description = "Endpoints para gerenciamento de filmes")
public class FilmeController {

    @Autowired
    private FilmeService filmeService;

    @Autowired
    private TmdbService tmdbService;

    @GetMapping
    @Operation(summary = "Lista todos os filmes")
    public ResponseEntity<List<FilmeResponseDTO>> listarFilmes() {
        return ResponseEntity.ok(filmeService.listarFilmes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca filme por ID")
    public ResponseEntity<FilmeResponseDTO> buscarFilmePorId(@PathVariable UUID id) {
        return ResponseEntity.ok(filmeService.buscarFilmePorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo filme")
    public ResponseEntity<FilmeResponseDTO> criarFilme(@RequestBody @Valid FilmeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(filmeService.criarFilme(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um filme")
    public ResponseEntity<FilmeResponseDTO> atualizarFilme(@PathVariable UUID id,
            @RequestBody @Valid FilmeRequestDTO dto) {
        return ResponseEntity.ok(filmeService.atualizarFilme(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um filme")
    public ResponseEntity<Void> deletarFilme(@PathVariable UUID id) {
        filmeService.deletarFilme(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{filmeId}/categorias/{categoriaId}")
    @Operation(summary = "Vincula uma categoria a um filme")
    public ResponseEntity<FilmeResponseDTO> vincularCategoria(
            @PathVariable UUID filmeId,
            @PathVariable Long categoriaId) {
        return ResponseEntity.ok(filmeService.vincularCategoria(filmeId, categoriaId));
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Busca filmes por categoria")
    public ResponseEntity<List<FilmeResponseDTO>> buscarFilmesPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(filmeService.buscarFilmesPorCategoria(categoriaId));
    }

    @GetMapping("/tmdb/{tmdbId}")
    @Operation(summary = "Endpoint temporário para testar conexão com o TMDB")
    public ResponseEntity<TmdbFilmeDetalhesDTO> testarTmdb(@PathVariable Long tmdbId) {
        TmdbFilmeDetalhesDTO detalhes = tmdbService.buscarFilmeExterno(tmdbId);
        return ResponseEntity.ok(detalhes);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busca filmes no banco local e na API do TMDB de forma unificada")
    public ResponseEntity<List<FilmeResponseDTO>> buscarFilmes(@RequestParam String query) {
        return ResponseEntity.ok(filmeService.buscarCatalogoUnificado(query));
    }

    @Operation(summary = "Busca o elenco de um filme pelo ID do TMDB", description = "Retorna a lista de atores e personagens direto da API externa")
    @GetMapping("/{tmdbId}/elenco")
    public ResponseEntity<TmdbCreditosResponseDTO> obterElenco(@PathVariable Long tmdbId) {
        // Chama o método do serviço que criamos ali em cima
        TmdbCreditosResponseDTO elenco = tmdbService.buscarElencoDoFilme(tmdbId);

        return ResponseEntity.ok(elenco);
    }

    @GetMapping("/tmdb-detalhes/{tmdbId}")
    @Operation(summary = "Busca filme pelos detalhes do TMDB")
    public ResponseEntity<TmdbFilmeDetalhesDTO> buscarFilmePorTmdbId(@PathVariable Long tmdbId) {
        System.out.println(">>> ESTA MERDA DE REQUISIÇÃO CHEGOU NO BACKEND. ID RECEBIDO: AGORA VAAAAAI POURRA " + tmdbId);
        return ResponseEntity.ok(tmdbService.buscarFilmeExterno(tmdbId));
    }
}