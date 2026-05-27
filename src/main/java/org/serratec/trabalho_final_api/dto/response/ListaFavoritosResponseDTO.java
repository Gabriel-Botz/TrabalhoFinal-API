package org.serratec.trabalho_final_api.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Usuario;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({ "id", "nomeLista", "privada", "dataCriacao", "usuario", "filmes", "series" })
public class ListaFavoritosResponseDTO {

    private UUID id;
    private String nomeLista;
    private Boolean privada;
    private LocalDate dataCriacao;
    private Usuario usuario;
    private List<FilmeResponseDTO> filmes;
    private List<SeriesResponseDTO> series;

}
