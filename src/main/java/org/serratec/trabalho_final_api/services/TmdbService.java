package org.serratec.trabalho_final_api.services;

import org.serratec.trabalho_final_api.dto.response.TmdbDetalhesDTO;
import org.serratec.trabalho_final_api.dto.response.TmdbResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TmdbService {

    private final WebClient webClient;//USADO PRA FAZER REQUISIÇÃO PRA FORA DA APLICAÇÃO (NO CASO NOSSA API) :)

    private final String apiKey = "ae41581a9c0c6cabd2cc9bcf5961ba1b"; //CHAVE DA NOSSA API :)

    public TmdbService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.themoviedb.org/3").build();//DIFININFO O BASE URL DA API :)
    }

    public TmdbDetalhesDTO buscarFilmeExterno(Long tmdbId) {
        try {
            return this.webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/" + tmdbId)
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "pt-BR")
                            .queryParam("append_to_response", "release_dates")
                            .build())
                    .retrieve()
                    .bodyToMono(TmdbDetalhesDTO.class) // Garanta que está convertendo para a classe aqui
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TmdbResponseDTO pesquisarFilmesNoTmdb(String query) {
        try {
            return this.webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/movie") // Endpoint de busca do TMDB
                            .queryParam("api_key", apiKey)
                            .queryParam("query", query)
                            .queryParam("language", "pt-BR")
                            .build())
                    .retrieve()
                    .bodyToMono(TmdbResponseDTO.class) // O Spring descompacta o JSON direto no nosso DTO Tradutor
                    .block();
        } catch (Exception e) {
            // Se a chamada falhar, retorna um objeto vazio para não travar a busca do banco local
            return new TmdbResponseDTO();
        }
    }
}