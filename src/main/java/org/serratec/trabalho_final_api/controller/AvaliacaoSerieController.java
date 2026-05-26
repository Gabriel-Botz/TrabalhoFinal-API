package org.serratec.trabalho_final_api.controller;

import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.dto.request.AvaliacaoSerieRequestDTO;
import org.serratec.trabalho_final_api.dto.response.AvaliacaoSerieResponseDTO;
import org.serratec.trabalho_final_api.services.AvaliacaoSerieService;
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
@RequestMapping("/avaliacoesSeries")
@Tag(name = "Avaliações de Séries", description = "Endpoints para gerenciamento das avaliações de séries")
public class AvaliacaoSerieController {

    @Autowired AvaliacaoSerieService service;

    @GetMapping
    @Operation(summary = "Listar avaliações de séries", description = "Retorna todas as avaliações de séries cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<AvaliacaoSerieResponseDTO>> findAll(){

        List<AvaliacaoSerieResponseDTO> avaliacoes = service.findAll();

        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar avaliação por ID", description = "Retorna uma avaliação especifica de série")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Avaliação encontrada"),
                    @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<AvaliacaoSerieResponseDTO> findById(@PathVariable UUID id){

        AvaliacaoSerieResponseDTO avaliacao = service.findById(id);

        return ResponseEntity.ok(avaliacao);
    }

    @PostMapping
    @Operation(summary = "Cadastrar avaliação de série", description = "Cria uma nova avaliação de série")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "201", description = "Avaliação criada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    })
    public ResponseEntity<AvaliacaoSerieResponseDTO> inserir(@Valid @RequestBody AvaliacaoSerieRequestDTO dto){

        AvaliacaoSerieResponseDTO criado = service.inserir(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar avaliação de série", description = "Atualiza uma avaliação existente")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Avaliação atualizada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<AvaliacaoSerieResponseDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody AvaliacaoSerieRequestDTO dto){

        AvaliacaoSerieResponseDTO atualizado = service.atualizar(id, dto);

        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir avaliação de série", description = "Remove uma avaliação de série pelo ID")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "204", description = "Avaliação removida com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<Void> deletar(@PathVariable UUID id){
        service.deletar(id);

        return ResponseEntity.noContent().build();
    }
}
