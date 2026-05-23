package org.serratec.trabalho_final_api.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.AvaliacaoFilme;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvaliacaoFilmeResponseDTO {

    private UUID id;

    private String nomeUsuario;

    private String nomeFilme;

    private Double nota;

    private String comentario;

    private LocalDate dataAvaliacao;

<<<<<<<< HEAD:src/main/java/org/serratec/trabalho_final_api/dto/response/AvaliacaoFilmeDTO.java
    public AvaliacaoFilmeDTO(AvaliacaoFilme avaliacaoFilme){
========
    public AvaliacaoFilmeResponseDTO(AvaliacaoFilme avaliacaoFilme){

>>>>>>>> 4ea3209eb1e3d7b91033a29108738bd9cb704fc4:src/main/java/org/serratec/trabalho_final_api/dto/response/AvaliacaoFilmeResponseDTO.java
        this.id = avaliacaoFilme.getId();
        this.nomeUsuario = avaliacaoFilme.getUsuario().getNome();
        this.nomeFilme = avaliacaoFilme.getFilme().getTitulo();
        this.nota = avaliacaoFilme.getNota();
        this.comentario = avaliacaoFilme.getComentario();
        this.dataAvaliacao = avaliacaoFilme.getDataAvaliacao();
    }

    
}   
