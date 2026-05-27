package org.serratec.trabalho_final_api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TmdbFilmesResponseDTO {

    @JsonProperty("results")
    private List<TmdbFilmeItem> resultados;

    @Getter
    @Setter
    public static class TmdbFilmeItem {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("title")
        private String title;

        @JsonProperty("overview")
        private String overview;

        @JsonProperty("release_date")
        private String releaseDate;

        @JsonProperty("poster_path")
        private String posterPath;

        @JsonProperty("backdrop_path")
        private String backdropPath;

        public FilmeResponseDTO paraFilmeResponseDTO() {
            FilmeResponseDTO dto = new FilmeResponseDTO();
            dto.setTmdbId(this.id);
            dto.setTitulo(this.title);
            dto.setDescricao(this.overview);

            if (this.posterPath != null) {
                dto.setPoster("https://image.tmdb.org/t/p/w500" + this.posterPath);
            }

            if (this.backdropPath != null) {
                dto.setBackdrop("https://image.tmdb.org/t/p/original" + this.backdropPath);
            }

            if (this.releaseDate != null && !this.releaseDate.isEmpty()) {
                try {
                    dto.setDataLancamento(java.time.LocalDate.parse(this.releaseDate));
                } catch (Exception e) {
                    dto.setDataLancamento(null);
                }
            }

            dto.setNotaMedia(0.0);
            return dto;
        }
    }
}