package org.serratec.trabalho_final_api.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.AvaliacaoSerie;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvaliacaoSerieResponseDTO {

    private UUID id;

    private String nomeUsuario;

    private String nomeSerie;

    private Double nota;

    private String comentario;

    private LocalDate dataAvaliacao;

    public AvaliacaoSerieResponseDTO(AvaliacaoSerie avaliacao){

        this.id = avaliacao.getId();
        this.nomeUsuario = avaliacao.getUsuario().getNome();
        this.nomeSerie = avaliacao.getSerie().getTitulo();
        this.nota = avaliacao.getNota();
        this.comentario = avaliacao.getComentario();
        this.dataAvaliacao = avaliacao.getDataAvaliacao();
    }
}
