package org.serratec.trabalho_final_api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TmdbResponseDTO {

    // O TMDB sempre devolve os resultados dentro de uma lista chamada "results"
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
        private String releaseDate; // Vem como texto "AAAA-MM-DD" do TMDB

        // Esse método faz a mágica de adaptar o formato do TMDB para o SEU FilmeResponseDTO
        public FilmeResponseDTO paraFilmeResponseDTO() {
            FilmeResponseDTO dto = new FilmeResponseDTO();
            // Como é um filme vindo de fora, o ID do nosso banco (UUID) fica nulo temporariamente
            dto.setTmdbId(this.id);
            dto.setTitulo(this.title);
            dto.setDescricao(this.overview);

            // Tratamento simples para evitar quebra caso o filme não tenha data de lançamento
            if (this.releaseDate != null && !this.releaseDate.isEmpty()) {
                try {
                    dto.setDataLancamento(java.time.LocalDate.parse(this.releaseDate));
                } catch (Exception e) {
                    dto.setDataLancamento(null);
                }
            }

            dto.setNotaMedia(0.0); // Ou mapear o "vote_average" do TMDB se seu front exibir
            return dto;
        }
    }
}