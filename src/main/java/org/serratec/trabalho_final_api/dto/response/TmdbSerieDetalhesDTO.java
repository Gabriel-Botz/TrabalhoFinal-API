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

    @JsonProperty("number_of_seasons")
    private Integer quantidadeTemporadas;

    @JsonProperty("number_of_episodes")
    private Integer quantidadeEpisodios;

    @JsonProperty("content_ratings")
    private ContentRatingsResult contentRatings;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentRatingsResult {
        @JsonProperty("results")
        private List<RatingResult> results;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RatingResult {
        @JsonProperty("iso_3166_1")
        private String isoCodigo;

        @JsonProperty("rating")
        private String rating;
    }
}