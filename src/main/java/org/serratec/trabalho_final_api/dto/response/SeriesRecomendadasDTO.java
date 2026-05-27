package org.serratec.trabalho_final_api.dto.response;

import org.serratec.trabalho_final_api.domain.Categoria;
import org.serratec.trabalho_final_api.domain.Series;
import java.util.List;
import java.util.UUID;


public class SeriesRecomendadasDTO {
        UUID id;
        String titulo;
        Double notaMedia;
        List<String> categoria;

        public SeriesRecomendadasDTO(Series series) {
            this.id = series.getId();
            this.titulo = series.getTitulo();
            this.notaMedia = series.getNotaMedia();
            this.categoria = series.getCategorias()
                    .stream()
                    .map(Categoria::getNome)
                    .toList();
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

    public Double getNotaMedia() {
        return notaMedia;
    }

    public void setNotaMedia(Double notaMedia) {
        this.notaMedia = notaMedia;
    }

    public List<String> getCategoria() {
        return categoria;
    }

    public void setCategoria(List<String> categoria) {
        this.categoria = categoria;
    }
}
