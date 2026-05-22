package org.serratec.TrabalhoFinal_API.controller;

import java.util.List;
import java.util.UUID;

import org.serratec.TrabalhoFinal_API.dto.request.AvaliacaoSerieDTORequest;
import org.serratec.TrabalhoFinal_API.dto.response.AvaliacaoSerieDTOResponse;
import org.serratec.TrabalhoFinal_API.services.AvaliacaoSerieService;
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

import jakarta.validation.Valid;


@RestController
@RequestMapping("/avaliacoesSeries")
public class AvaliacaoSerieController {

    @Autowired AvaliacaoSerieService service;

    @GetMapping
    public ResponseEntity<List<AvaliacaoSerieDTOResponse>> findAll(){

        List<AvaliacaoSerieDTOResponse> avaliacoes = service.findAll();

        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoSerieDTOResponse> findById(@PathVariable UUID id){

        AvaliacaoSerieDTOResponse avaliacao = service.findById(id);

        return ResponseEntity.ok(avaliacao);
    }

    @PostMapping
    public ResponseEntity<AvaliacaoSerieDTOResponse> inserir(@Valid @RequestBody AvaliacaoSerieDTORequest dto){

        AvaliacaoSerieDTOResponse criado = service.inserir(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoSerieDTOResponse> atualizar(@PathVariable UUID id, @Valid @RequestBody AvaliacaoSerieDTORequest dto){

        AvaliacaoSerieDTOResponse atualizado = service.atualizar(id, dto);

        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id){
        service.deletar(id);

        return ResponseEntity.noContent().build();
    }
}
