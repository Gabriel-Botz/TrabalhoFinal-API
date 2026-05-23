package org.serratec.TrabalhoFinal_API.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import org.serratec.TrabalhoFinal_API.domain.AvaliacaoFilme;

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

<<<<<<<< HEAD:src/main/java/org/serratec/TrabalhoFinal_API/dto/response/AvaliacaoFilmeDTO.java
    public AvaliacaoFilmeDTO(AvaliacaoFilme avaliacaoFilme){
========
    public AvaliacaoFilmeResponseDTO(AvaliacaoFilme avaliacaoFilme){

>>>>>>>> 7555498c10fd1d441f42e418ded059f599ed2689:src/main/java/org/serratec/TrabalhoFinal_API/dto/response/AvaliacaoFilmeResponseDTO.java
        this.id = avaliacaoFilme.getId();
        this.nomeUsuario = avaliacaoFilme.getUsuario().getNome();
        this.nomeFilme = avaliacaoFilme.getFilme().getTitulo();
        this.nota = avaliacaoFilme.getNota();
        this.comentario = avaliacaoFilme.getComentario();
        this.dataAvaliacao = avaliacaoFilme.getDataAvaliacao();
    }

    
}   
