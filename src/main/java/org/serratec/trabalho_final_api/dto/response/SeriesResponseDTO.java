package org.serratec.trabalho_final_api.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Series;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "titulo", "descricao", "temporadas", "episodios", "dataLancamento", "notaMedia" })
public class SeriesResponseDTO {
    private UUID id;
    private String titulo;
    private String descricao;
    private Integer temporadas;
    private Integer episodios;
    private LocalDate dataLancamento;
    private Double notaMedia;

    public SeriesResponseDTO() {
    }

    public SeriesResponseDTO(Series series) {
        this.id = series.getId();
        this.titulo = series.getTitulo();
        this.descricao = series.getDescricao();
        this.temporadas = series.getTemporadas();
        this.episodios = series.getEpisodios();
        this.dataLancamento = series.getDataLancamento();
        this.notaMedia = series.getNotaMedia();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getTemporadas() {
        return temporadas;
    }

    public void setTemporadas(Integer temporadas) {
        this.temporadas = temporadas;
    }

    public Integer getEspisodios() {
        return episodios;
    }

    public void setEpisodios(Integer episodios) {
        this.episodios = episodios;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Double getNotaMedia() {
        return notaMedia;
    }

    public void setNotaMedia(Double notaMedia) {
        this.notaMedia = notaMedia;
    }
}
