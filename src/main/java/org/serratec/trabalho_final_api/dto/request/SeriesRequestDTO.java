package org.serratec.trabalho_final_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class SeriesRequestDTO {

    @NotBlank(message = "Titulo é obrigatório")
    @NotNull(message = "Titulo não pode ser nulo")
    private String titulo;

    @NotBlank(message = "descrição é obrigatório")
    @NotNull(message = "descrição pode ser nula")
    private String descricao;


    @NotNull(message = "temporadas não pode ser nulo")
    private Integer temporadas;


    @NotNull(message = "episodios não pode ser nulo")
    private Integer episodios;


    @NotNull(message = "Data de lançamento não pode ser nulo")
    private LocalDate dataLancamento;


    @NotNull(message = "Nota media não pode ser nulo")
    private Double notaMedia;

    private List<Long> idCategorias;

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

    public Integer getEpisodios() {
        return episodios;
    }

    public void setEpisodios(Integer espisodios) {
        this.episodios = espisodios;
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

    public List<Long> getIdCategorias() {
        return idCategorias;
    }

    public void setIdCategorias(List<Long> idCategorias) {
        this.idCategorias = idCategorias;
    }
}
