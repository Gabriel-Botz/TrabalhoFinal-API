package org.serratec.TrabalhoFinal_API.dto.rquestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class SeriesRequestDTO {

    @NotBlank(message = "Titulo é obrigatório")
    @NotNull(message = "Titulo não pode ser nulo")
    private String titulo;

    @NotBlank(message = "descrição é obrigatório")
    @NotNull(message = "descrição pode ser nula")
    private String descricao;

    @NotBlank(message = "temporadas é obrigatório")
    @NotNull(message = "temporadas não pode ser nulo")
    private Integer temporadas;

    @NotBlank(message = "episodios é obrigatório")
    @NotNull(message = "episodios não pode ser nulo" )
    private Integer espisodios;

    @NotBlank(message = "Data de lançamento é obrigatório")
    @NotNull(message = "Data de lançamento não pode ser nulo")
    private LocalDate dataLancamento;

    @NotBlank(message = "Nota media é obrigatório")
    @NotNull(message = "Nota media não pode ser nulo")
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
