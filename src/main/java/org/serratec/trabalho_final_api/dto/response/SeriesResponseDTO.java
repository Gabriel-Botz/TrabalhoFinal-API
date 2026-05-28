package org.serratec.trabalho_final_api.dto.response;

import org.serratec.trabalho_final_api.domain.Series;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
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