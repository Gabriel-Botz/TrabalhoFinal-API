package org.serratec.trabalho_final_api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbCreditosResponseDTO {

    @JsonProperty("cast")
    private List<AtorItem> elenco;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AtorItem {
        @JsonProperty("name")
        private String nomeAtor;

        @JsonProperty("character")
        private String nomePersonagem;

        @JsonProperty("profile_path")
        private String caminhoFoto;
    }
}