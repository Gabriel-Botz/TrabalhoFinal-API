package org.serratec.trabalho_final_api.dto.request;

import java.time.LocalDate;

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
public class ListaFavoritosRequestDTO {

    @Schema(description = "Nome da lista de favoritos", example = "Filmes de Ação")
    private String nomeLista;
    
    @Schema(description = "Indica se a lista é privada ou pública", example = "true")
    private Boolean privada;

    @Schema(description = "Usuário dono da lista de favoritos", example = "jhondoe")
    private Usuario usuario;

}
