package org.serratec.trabalho_final_api.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListaFavoritosResponseDTO {

    @Schema(description = "ID da lista de favoritos", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Nome da lista de favoritos", example = "Filmes de Ação")
    private String nomeLista;

    @Schema(description = "Indica se a lista é privada ou pública", example = "true")
    private Boolean privada;

    @Schema(description = "Data de criação da lista de favoritos", example = "2026-05-25")
    private LocalDate dataCriacao;

    @Schema(description = "Usuário dono da lista de favoritos")
    private Usuario usuario;

    @Schema(description = "Lista de filmes favoritos")
    private List<FilmeResponseDTO> filmes;

    @Schema(description = "Lista de séries favoritas")
    private List<SeriesResponseDTO> series;

}
