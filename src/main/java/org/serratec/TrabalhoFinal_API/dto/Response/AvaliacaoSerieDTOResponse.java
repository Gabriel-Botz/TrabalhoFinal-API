package org.serratec.TrabalhoFinal_API.dto.Response;

import java.time.LocalDate;
import java.util.UUID;

import org.serratec.TrabalhoFinal_API.domain.AvaliacaoSerie;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvaliacaoSerieDTOResponse {

    private UUID id;

    private String nomeUsuario;

    private String nomeFilme;

    private Double nota;

    private String comentario;

    private LocalDate dataAvaliacao;

    public AvaliacaoSerieDTOResponse(AvaliacaoSerie avaliacaoSerie){

        this.id = avaliacaoSerie.getId();
        this.nomeUsuario = avaliacaoSerie.getUsuario().getNome();
        this.nomeFilme = avaliacaoSerie.getSerie().getTitulo();
        this.nota = avaliacaoSerie.getNota();
        this.comentario = avaliacaoSerie.getComentario();
        this.dataAvaliacao = avaliacaoSerie.getDataAvaliacao();
    }
}
