package org.serratec.trabalho_final_api.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.AvaliacaoSerie;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({ "id", "nomeUsuario", "nomeSerie", "nota", "comentario", "dataAvaliacao" })
public class AvaliacaoSerieResponseDTO {

    @Schema(description = "ID de avaliação da série", example = "540e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Nome do usuário que realizou a avaliação, example = Paulo Victor")
    private String nomeUsuario;

    @Schema(description = "Nome da série avaliada", example = "Reacher")
    private String nomeSerie;

    @Schema(description = "Nota atribuida à série", example = "9.0")
    private Double nota;

    @Schema(description = "Comentário da avaliação", example = "Excelente roteiro e atuação.")
    private String comentario;

    @Schema(description = "Data da avaliação", example = "2026-05-25")
    private LocalDate dataAvaliacao;

    public AvaliacaoSerieResponseDTO(AvaliacaoSerie avaliacao) {

        this.id = avaliacao.getId();
        this.nomeUsuario = avaliacao.getUsuario().getNome();
        this.nomeSerie = avaliacao.getSeries().getTitulo();
        this.nota = avaliacao.getNota();
        this.comentario = avaliacao.getComentario();
        this.dataAvaliacao = avaliacao.getDataAvaliacao();
    }
}
