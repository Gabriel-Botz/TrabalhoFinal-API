package org.serratec.TrabalhoFinal_API.controller;

import org.serratec.TrabalhoFinal_API.dto.responseDTO.SeriesResponseDTO;
import org.serratec.TrabalhoFinal_API.services.SeriesServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<SeriesResponseDTO> filtrarPorTitulo(@PathVariable String titulo) {
        return ResponseEntity.ok(seriesServices.ListarSeriePorTitulo(titulo));
    }










}
