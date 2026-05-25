package org.serratec.trabalho_final_api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbFilmeDetalhesDTO {

    @JsonProperty("runtime")
    private Integer runtime;

    @JsonProperty("release_dates")
    private ReleaseDatesContainer releaseDates;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReleaseDatesContainer {
        @JsonProperty("results")
        private List<PaisResult> results;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaisResult {
        @JsonProperty("iso_3166_1")
        private String isoCodigo;

        @JsonProperty("release_dates")
        private List<CertificacaoItem> releaseDates;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CertificacaoItem {
        @JsonProperty("certification")
        private String certification;
    }
}