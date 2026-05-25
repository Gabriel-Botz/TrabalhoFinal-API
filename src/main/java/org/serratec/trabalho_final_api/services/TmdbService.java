package org.serratec.trabalho_final_api.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TmdbService {

    private final WebClient webClient;//USADO PRA FAZER REQUISIÇÃO PRA FORA DA APLICAÇÃO (NO CASO NOSSA API) :)

    private final String apiKey = "ae41581a9c0c6cabd2cc9bcf5961ba1b"; //CHAVE DA NOSSA API :)

    public TmdbService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.themoviedb.org/3").build();//DIFININFO O BASE URL DA API :)
    }

    public String buscarFilmeExterno(Long tmdbId) {
        try {
            return this.webClient.get()//INFORMANDO QUE É UMA REQUISIÇÃO DO TIPO GET :)
                    .uri(uriBuilder -> uriBuilder//CONSTRÓI O RESTANTE DO ENDEREÇO DA REQUISIÇÃO, INCLUINDO O ID DO FILME E A CHAVE DE AUTENTICAÇÃO :)
                            .path("/movie/" + tmdbId)
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "pt-BR")
                            .build())
                            .retrieve()//VALIDA A URL E DISPARA A REQUISIÇÃO :)
                            .bodyToMono(String.class)//DIZ COMO QUEREMOS RECEBER A RESPOSTA, COMO A API DO TMDB RESPONDE COM UM TEXTO EM FORMATO JSON, NÓS PEDIMOS PARA O SPRING CAPTURAR ESSE JSON BRUTO COMO UMA STRIN COMUM DO JAVA :)
                            .block();//AVISA PRO JAVA NÃO SER APRESSADINHO E ESPERAR A REQUISIÇÃO DA API CHEGAR, ANTES DE SEGUIR PRA PRÓXIMA LINHA
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar o TMDB: " + e.getMessage());
        }
    }
}