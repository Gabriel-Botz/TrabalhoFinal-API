package org.serratec.trabalho_final_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class SeriesRequestDTO {

    @NotBlank(message = "Título é obrigatório.")
    @NotNull(message = "Título não pode ser nulo.")
    private String titulo;

    @NotBlank(message = "Descrição é obrigatório.")
    @NotNull(message = "Descrição pode ser nula.")
    private String descricao;

    @NotBlank(message = "Temporadas é obrigatório.")
    @NotNull(message = "Temporadas não pode ser nulo.")
    private Integer temporadas;

    @NotBlank(message = "Episódios é obrigatório.")
    @NotNull(message = "Episódios não pode ser nulo.")
    private Integer espisodios;

    @NotBlank(message = "Data de lançamento é obrigatório.")
    @NotNull(message = "Data de lançamento não pode ser nulo.")
    private LocalDate dataLancamento;

    @NotBlank(message = "Nota média é obrigatório.")
    @NotNull(message = "Nota média não pode ser nulo.")
    private Double notaMedia;

    public SeriesRequestDTO() {
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
        return espisodios;
    }

    public void setEspisodios(Integer espisodios) {
        this.espisodios = espisodios;
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
