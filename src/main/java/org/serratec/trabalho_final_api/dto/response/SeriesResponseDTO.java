package org.serratec.trabalho_final_api.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Series;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeriesResponseDTO {
    private UUID id;
    private String titulo;
    private String descricao;
    private Integer temporadas;
    private Integer episodios;
    private LocalDate dataLancamento;
    private Double notaMedia;
    private String poster;
    private String backdrop;
    private Long tmdbId;

    public SeriesResponseDTO(Series series) {
        this.id = series.getId();
        this.titulo = series.getTitulo();
        this.descricao = series.getDescricao();
        this.temporadas = series.getTemporadas();
        this.episodios = series.getEpisodios();
        this.dataLancamento = series.getDataLancamento();
        this.notaMedia = series.getNotaMedia();
        this.poster = series.getPoster();
        this.tmdbId = series.getTmdbId();
        this.backdrop = series.getBackdrop();
    }
}