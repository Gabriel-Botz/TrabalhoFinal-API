package org.serratec.TrabalhoFinal_API.dto.responseDTO;

import java.time.LocalDate;

public class SeriesResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private Integer temporadas;
    private Integer espisodios;
    private LocalDate dataLancamento;
    private Double notaMedia;

    public SeriesResponseDTO() {
    }

    public SeriesResponseDTO(Long id, String titulo, String descricao, Integer temporadas, Integer espisodios, LocalDate dataLancamento, Double notaMedia) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.temporadas = temporadas;
        this.espisodios = espisodios;
        this.dataLancamento = dataLancamento;
        this.notaMedia = notaMedia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
