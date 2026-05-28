package org.serratec.trabalho_final_api.services;

import org.serratec.trabalho_final_api.dto.response.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TmdbService {

    private final WebClient webClient; // USADO PRA FAZER REQUISIÇÃO PRA FORA DA APLICAÇÃO :)

    private final String apiKey = "ae41581a9c0c6cabd2cc9bcf5961ba1b"; // CHAVE DA NOSSA API :)

    public TmdbService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.themoviedb.org/3").build(); // DEFININDO O BASE URL DA API :)
    }

    // ==========================================
    //            MÉTODOS DE FILMES
    // ==========================================

    public TmdbFilmeDetalhesDTO buscarFilmeExterno(Long tmdbId) {
        try {
            return this.webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/" + tmdbId)
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "pt-BR")
                            .queryParam("append_to_response", "release_dates")
                            .build())
                    .retrieve()
                    .bodyToMono(TmdbFilmeDetalhesDTO.class)
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TmdbFilmesResponseDTO pesquisarFilmesNoTmdb(String query) {
        try {
            return this.webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/movie")
                            .queryParam("api_key", apiKey)
                            .queryParam("query", query)
                            .queryParam("language", "pt-BR")
                            .build())
                    .retrieve()
                    .bodyToMono(TmdbFilmesResponseDTO.class)
                    .block();
        } catch (Exception e) {
            return new TmdbFilmesResponseDTO();
        }
    }

    // ==========================================
    //            MÉTODOS DE SÉRIES
    // ==========================================

    /**
     * Pesquisa séries no TMDB pelo texto informado (Query)
     */
    public TmdbSerieResponseDTO pesquisarSeriesNoTmdb(String query) {
        try {
            return this.webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/tv") // Endpoint de busca de séries/TV no TMDB
                            .queryParam("api_key", apiKey)
                            .queryParam("query", query)
                            .queryParam("language", "pt-BR")
                            .build())
                    .retrieve()
                    .bodyToMono(TmdbSerieResponseDTO.class)
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return new TmdbSerieResponseDTO();
        }
    }

    /**
     * Busca os detalhes de uma série específica para obter duração e classificações
     */
    public TmdbSerieDetalhesDTO buscarSerieExterna(Long tmdbId) {
        try {
            return this.webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/tv/" + tmdbId) // Endpoint de detalhes da série no TMDB
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "pt-BR")
                            .queryParam("append_to_response", "content_ratings") // Traz as classificações etárias
                            .build())
                    .retrieve()
                    .bodyToMono(TmdbSerieDetalhesDTO.class)
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TmdbCreditosResponseDTO buscarElencoDoFilme(Long tmdbId) {
        try {
            return this.webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/" + tmdbId + "/credits")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "pt-BR")
                            .build())
                    .retrieve()
                    .bodyToMono(TmdbCreditosResponseDTO.class)
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TmdbCreditosResponseDTO buscarElencoDaSerie(Long tmdbId) {
        try {
            return this.webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/tv/" + tmdbId + "/credits") // Aqui muda para /tv/ por ser série
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "pt-BR")
                            .build())
                    .retrieve()
                    .bodyToMono(TmdbCreditosResponseDTO.class)
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}