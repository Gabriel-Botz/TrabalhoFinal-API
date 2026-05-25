package org.serratec.trabalho_final_api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbSerieDetalhesDTO {

    // Vem como uma lista de inteiros (ex: [45, 50])
    @JsonProperty("episode_run_time")
    private List<Integer> episodeRunTime;

    @JsonProperty("content_ratings")
    private ContentRatingsContainer contentRatings;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentRatingsContainer {
        @JsonProperty("results")
        private List<RatingResult> results;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RatingResult {
        @JsonProperty("iso_3166_1")
        private String isoCodigo; // Procuramos por "BR"

        @JsonProperty("rating")
        private String rating; // Ex: "14", "16", "L"
    }

    // Método auxiliar para pegar a primeira duração da lista, se existir
    public Integer getDuracaoMedia() {
        if (episodeRunTime != null && !episodeRunTime.isEmpty()) {
            return episodeRunTime.get(0);
        }
        return null;
    }
}