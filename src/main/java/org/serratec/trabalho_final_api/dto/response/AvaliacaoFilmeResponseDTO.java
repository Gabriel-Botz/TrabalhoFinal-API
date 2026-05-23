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

    public AvaliacaoFilmeResponseDTO(AvaliacaoFilme avaliacaoFilme) {
        this.id = avaliacaoFilme.getId();
        this.nomeUsuario = avaliacaoFilme.getUsuario().getNome();
        this.nomeFilme = avaliacaoFilme.getFilme().getTitulo();
        this.nota = avaliacaoFilme.getNota();
        this.comentario = avaliacaoFilme.getComentario();
        this.dataAvaliacao = avaliacaoFilme.getDataAvaliacao();
    }

}
