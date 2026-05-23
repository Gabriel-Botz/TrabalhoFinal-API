package org.serratec.trabalho_final_api.dto.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListaFavoritosRequestDTO {

    private String nomeLista;
    private Boolean privada;
    private LocalDate dataCriacao;

}
