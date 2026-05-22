package org.serratec.TrabalhoFinal_API.controller;

import org.serratec.TrabalhoFinal_API.domain.Series;
import org.serratec.TrabalhoFinal_API.dto.responseDTO.SeriesResponseDTO;
import org.serratec.TrabalhoFinal_API.dto.rquestDTO.SeriesRequestDTO;
import org.serratec.TrabalhoFinal_API.services.SeriesServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/series")
public class SeriesController {

    @Autowired
    private SeriesServices seriesServices;

    @GetMapping
    public ResponseEntity<List<SeriesResponseDTO>> listarSeries() {
        return ResponseEntity.ok(seriesServices.ListarTodasSeries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeriesResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(seriesServices.ListarSeriesPorId(id));
    }

    @GetMapping("/{titulo}")
    public ResponseEntity<SeriesResponseDTO> filtrarPorTitulo(@PathVariable String titulo) {
        return ResponseEntity.ok(seriesServices.ListarSeriePorTitulo(titulo));
    }

    @PostMapping
    public ResponseEntity<SeriesResponseDTO> inserirSeries(@RequestBody SeriesRequestDTO  seriesRequest) {

        SeriesResponseDTO seriesDTO = seriesServices.inserirSeries(seriesRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(seriesDTO);
    }

    @PutMapping
    public ResponseEntity<SeriesResponseDTO>
    atualizarSeries(@RequestBody SeriesRequestDTO seriesRequest,@PathVariable UUID id) {

        SeriesResponseDTO seriesDTO = seriesServices.atualizarSeries(seriesRequest,id);

        return ResponseEntity.ok(seriesDTO);

    }

    @DeleteMapping
    public ResponseEntity<Void> removerSeries(@PathVariable UUID id) {

        seriesServices.removerSeries(id);

        return ResponseEntity.noContent().build();
    }










}
