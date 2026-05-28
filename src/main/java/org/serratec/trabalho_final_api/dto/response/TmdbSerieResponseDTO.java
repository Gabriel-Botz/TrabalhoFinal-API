package org.serratec.trabalho_final_api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TmdbSerieResponseDTO { // Classe principal casando com o nome do arquivo

    // O TMDB joga as séries dentro de uma lista chamada "results"
    @JsonProperty("results")
    private List<TmdbSerieItem> resultados;

    @Getter
    @Setter
    public static class TmdbSerieItem {
        @JsonProperty("id")
        private Long id;

        // No TMDB, para séries o campo é "name" (em filmes é "title")
        @JsonProperty("name")
        private String name;

        @JsonProperty("overview")
        private String overview;

        // No TMDB, para séries o campo é "first_air_date" (em filmes é "release_date")
        @JsonProperty("first_air_date")
        private String firstAirDate;

        // Captura o caminho relativo da imagem da série
        @JsonProperty("poster_path")
        private String posterPath;

        @JsonProperty("backdrop_path")
        private String backdropPath;

        // Instancia a classe de saída oficial: SeriesResponseDTO
        public SeriesResponseDTO paraSeriesResponseDTO() {
            SeriesResponseDTO dto = new SeriesResponseDTO();
            dto.setTmdbId(this.id);
            dto.setTitulo(this.name);
            dto.setDescricao(this.overview);

            // Constrói a URL completa da capa da série para o front
            if (this.posterPath != null && !this.posterPath.isEmpty()) {
                dto.setPoster("https://image.tmdb.org/t/p/w500" + this.posterPath);
            } else {
                dto.setPoster(null);
            }

            if (this.backdropPath != null && !this.backdropPath.isEmpty()) {
                dto.setBackdrop("https://image.tmdb.org/t/p/original" + this.backdropPath);
            }

            if (this.firstAirDate != null && !this.firstAirDate.isEmpty()) {
                try {
                    dto.setDataLancamento(java.time.LocalDate.parse(this.firstAirDate));
                } catch (Exception e) {
                    dto.setDataLancamento(null);
                }
            }
            dto.setNotaMedia(0.0);
            return dto;
        }
    }
}