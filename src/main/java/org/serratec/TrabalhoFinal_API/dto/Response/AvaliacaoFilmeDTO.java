package org.serratec.TrabalhoFinal_API.dto.Response;

import java.time.LocalDate;
import java.util.UUID;

import org.serratec.TrabalhoFinal_API.domain.AvaliacaoFilme;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvaliacaoFilmeDTO {

    private UUID id;

    private String nomeUsuario;

    private String nomeFilme;

    private Double nota;

    private String comentario;

    private LocalDate dataAvaliacao;

    public AvaliacaoFilmeDTO(AvaliacaoFilme avaliacaoFilme){
        this.id = avaliacaoFilme.getId();
        this.nomeUsuario = avaliacaoFilme.getUsuario().getNome();
        this.nomeFilme = avaliacaoFilme.getFilme().getTitulo();
        this.nota = avaliacaoFilme.getNota();
        this.comentario = avaliacaoFilme.getComentario();
        this.dataAvaliacao = avaliacaoFilme.getDataAvaliacao();
    }

    
}   
