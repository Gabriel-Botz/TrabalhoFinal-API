package org.serratec.TrabalhoFinal_API.controller;

import java.util.List;
import java.util.UUID;

import org.serratec.TrabalhoFinal_API.dto.Request.AvaliacaoFilmeDTORequest;
import org.serratec.TrabalhoFinal_API.dto.Response.AvaliacaoFilmeDTOResponse;
import org.serratec.TrabalhoFinal_API.services.AvaliacaoFilmeService;
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
@RequestMapping("/avaliacoesFilmes")
public class AvaliacaoFilmeController {

    @Autowired AvaliacaoFilmeService service;

    @GetMapping
    public ResponseEntity<List<AvaliacaoFilmeDTOResponse>> findAll(){

        List<AvaliacaoFilmeDTOResponse> avaliacoes = service.findAll();

        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoFilmeDTOResponse> findById(@PathVariable UUID id){

        AvaliacaoFilmeDTOResponse avaliacao = service.findById(id);

        return ResponseEntity.ok(avaliacao);
    }

    @PostMapping
    public ResponseEntity<AvaliacaoFilmeDTOResponse> inserir(@Valid @RequestBody AvaliacaoFilmeDTORequest dto){

        AvaliacaoFilmeDTOResponse criado = service.inserir(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}") 
    public ResponseEntity<AvaliacaoFilmeDTOResponse> atualizar(@PathVariable UUID id, @Valid @RequestBody AvaliacaoFilmeDTORequest dto){
        
        AvaliacaoFilmeDTOResponse atualizado = service.atualizar(id, dto);

        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> deletar(@PathVariable UUID id){
        service.deletar(id);

            return ResponseEntity.noContent().build();
    }
}
