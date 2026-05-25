package org.serratec.trabalho_final_api.controller;

import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.dto.request.AvaliacaoFilmeRequestDTO;
import org.serratec.trabalho_final_api.dto.response.AvaliacaoFilmeResponseDTO;
import org.serratec.trabalho_final_api.services.AvaliacaoFilmeService;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/avaliacoesFilmes")
@Tag(name = "Avaliações de Filmes", description = "Endpoints para gerenciamento das avaliações dos filmes")
public class AvaliacaoFilmeController {

    @Autowired
    AvaliacaoFilmeService service;

    @GetMapping
    @Operation(summary = "Listar avaliações", description = "Retorna todas as avaliações cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<AvaliacaoFilmeResponseDTO>> findAll() {

        List<AvaliacaoFilmeResponseDTO> avaliacoes = service.findAll();

        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar avaliações por ID", description = "Retorna uma avaliação específica")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Avaliação encontrada"),
                    @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<AvaliacaoFilmeResponseDTO> findById(@PathVariable UUID id) {

        AvaliacaoFilmeResponseDTO avaliacao = service.findById(id);

        return ResponseEntity.ok(avaliacao);
    }

    @PostMapping
    @Operation(summary = "Cria uma nova avaliação de filme")
    @ApiResponse(responseCode = "201", description = "Avaliação criada com sucesso")
    public ResponseEntity<AvaliacaoFilmeResponseDTO> inserir(@Valid @RequestBody AvaliacaoFilmeRequestDTO dto) {

        AvaliacaoFilmeResponseDTO criado = service.inserir(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar avaliações", description = "Atualiza os dados de uma avaliação")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Avaliação atualizada"),
                    @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<AvaliacaoFilmeResponseDTO> atualizar(@PathVariable UUID id,
            @Valid @RequestBody AvaliacaoFilmeRequestDTO dto) {

        AvaliacaoFilmeResponseDTO atualizado = service.atualizar(id, dto);

        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir avaliação", description = "Remove uma avaliação pelo ID")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "204", description = "Avaliação removida"),
                    @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);

        return ResponseEntity.noContent().build();
    }
}
