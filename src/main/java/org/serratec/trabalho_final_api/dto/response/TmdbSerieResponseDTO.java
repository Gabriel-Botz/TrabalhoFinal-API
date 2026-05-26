package org.serratec.trabalho_final_api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbSerieResponseDTO {

    @JsonProperty("results")
    private List<TmdbSerieItem> resultados;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmdbSerieItem {

        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("overview")
        private String overview;

        @JsonProperty("first_air_date")
        private String firstAirDate;

        // Tradutor oficial adaptado para usar SeriesResponseDTO sem quebrar
        public SeriesResponseDTO paraSerieResponseDTO() {
            SeriesResponseDTO dto = new SeriesResponseDTO();

            // Comentado porque a classe SeriesResponseDTO não possui o campo tmdbId
            // dto.setTmdbId(this.id);

            dto.setTitulo(this.name);
            dto.setDescricao(this.overview);

            if (this.firstAirDate != null && !this.firstAirDate.isEmpty()) {
                try {
                    dto.setDataLancamento(LocalDate.parse(this.firstAirDate));
                } catch (Exception e) {
                    dto.setDataLancamento(null);
                }
            }

            dto.setNotaMedia(0.0);
            return dto;
        }
    }
}