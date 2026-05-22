package org.serratec.TrabalhoFinal_API.dto.Response;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvaliacaoFilmeDTOResponse {

    private UUID id;

    private String nomeUsuario;

    private String nomeFilme;

    private Double nota;

    private String comentario;

    private LocalDate dataAvaliacao;

    public AvaliacaoFilmeDTOResponse(AvaliacaoFilmeDTOResponse av){

        this.id = av.getId();
        this.nomeUsuario = av.getUsuario().getNome();
        this.nomeFilme = av.getFilme().getTitulo();
        this.nota = av.getNota();
        this.comentario = av.getComentario();
        this.dataAvaliacao = av.getDataAvaliacao();
    }

    
}   
