package org.serratec.trabalho_final_api.controller;

import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.dto.request.SeriesRequestDTO;
import org.serratec.trabalho_final_api.dto.response.SeriesResponseDTO;
import org.serratec.trabalho_final_api.services.SeriesServices;
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
@RequestMapping("/series")
@Tag(name = "series", description = "Endpoints para buscar e adicionar series")
public class SeriesController {

        @Autowired
        private SeriesServices seriesServices;

        @Operation(summary = "listar todas as séries", description = "lista todas as séries do Banco de Dados")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Series econtradas com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Requisição Inválidade"),
                        @ApiResponse(responseCode = "401", description = "Não Autorizado"),
                        @ApiResponse(responseCode = "403", description = "Proibido"),
                        @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do Servidor")
        })
        @GetMapping
        public ResponseEntity<List<SeriesResponseDTO>> listarSeries() {
                return ResponseEntity.ok(seriesServices.ListarTodasSeries());
        }

        @Operation(summary = "lista uma Serie pelo ID", description = "lista uma Série especifica do Banco de Dados pelo ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Series econtradas com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Requisição Inválidade"),
                        @ApiResponse(responseCode = "401", description = "Não Autorizado"),
                        @ApiResponse(responseCode = "403", description = "Proibido"),
                        @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do Servidor")
        })
        @GetMapping("/{id}")
        public ResponseEntity<SeriesResponseDTO> findById(@PathVariable UUID id) {
                return ResponseEntity.ok(seriesServices.ListarSeriesPorId(id));
        }

        @Operation(summary = "lista uma Serie pelo titulo", description = "lista uma Série especifica do Banco de Dados pelo titulo")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Id Serie econtrado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Requisição Inválidade"),
                        @ApiResponse(responseCode = "401", description = "Não Autorizado"),
                        @ApiResponse(responseCode = "403", description = "Proibido"),
                        @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do Servidor")
        })
        @GetMapping("/{titulo}")
        public ResponseEntity<SeriesResponseDTO> filtrarPorTitulo(@PathVariable String titulo) {
                return ResponseEntity.ok(seriesServices.ListarSeriePorTitulo(titulo));
        }

        @Operation(summary = "Vincular uma serie a uma categoria", description = "vincular uma serie a uma categoria no Banco de Dados")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Series vinculada a categoria com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Requisição Inválidade"),
                        @ApiResponse(responseCode = "401", description = "Não Autorizado"),
                        @ApiResponse(responseCode = "403", description = "Proibido"),
                        @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do Servidor")
        })
        @PutMapping("/{id}/categoria/{idCategoria}")
        public ResponseEntity<SeriesResponseDTO> vincularCategoria(@PathVariable UUID id,
                        @PathVariable Long idCategoria) {
                return ResponseEntity.ok(seriesServices.vincularCategoria(id, idCategoria));
        }

        @Operation(summary = "Buscar categoria de series pelo Id ", description = "Buscar categoria de series do Banco de Dados pelo ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Categoria econtrada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Requisição Inválidade"),
                        @ApiResponse(responseCode = "401", description = "Não Autorizado"),
                        @ApiResponse(responseCode = "403", description = "Proibido"),
                        @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do Servidor")
        })
        @GetMapping("/categoria/{idCategoria}")
        public ResponseEntity<List<SeriesResponseDTO>> filtrarPorCategoria(@PathVariable Long idCategoria) {
                return ResponseEntity.ok(seriesServices.buscarPorCategoria(idCategoria));
        }

        @Operation(summary = "Inserir uma Serie", description = "Inserir uma Série no Banco de Dados")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Series inserida com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Requisição Inválidade"),
                        @ApiResponse(responseCode = "401", description = "Não Autorizado"),
                        @ApiResponse(responseCode = "403", description = "Proibido"),
                        @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do Servidor")
        })
        @PostMapping
        public ResponseEntity<SeriesResponseDTO> inserirSeries(@Valid @RequestBody SeriesRequestDTO seriesRequest) {

                SeriesResponseDTO seriesDTO = seriesServices.inserirSeries(seriesRequest);

                return ResponseEntity.status(HttpStatus.CREATED).body(seriesDTO);
        }

        @Operation(summary = "Atualizar uma Série ", description = "Atualiza os atributos de uma Série")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Serie atualizada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Requisição Inválidade"),
                        @ApiResponse(responseCode = "401", description = "Não Autorizado"),
                        @ApiResponse(responseCode = "403", description = "Proibido"),
                        @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do Servidor")
        })
        @PutMapping
        public ResponseEntity<SeriesResponseDTO> atualizarSeries(@Valid @RequestBody SeriesRequestDTO seriesRequest,
                        @PathVariable UUID id) {

                SeriesResponseDTO seriesDTO = seriesServices.atualizarSeries(seriesRequest, id);

                return ResponseEntity.ok(seriesDTO);
        }

        @Operation(summary = "Deletar uma serie pelo ID", description = "Deleta uma Série especifica do Banco de Dados pelo ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Series econtradas com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Requisição Inválidade"),
                        @ApiResponse(responseCode = "401", description = "Não Autorizado"),
                        @ApiResponse(responseCode = "403", description = "Proibido"),
                        @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do Servidor")
        })
        @DeleteMapping
        public ResponseEntity<Void> removerSeries(@PathVariable UUID id) {

                seriesServices.removerSeries(id);

                return ResponseEntity.noContent().build();
        }
}
