package org.serratec.TrabalhoFinal_API.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListaFavoritosResponseDTO {

    private UUID id;
    private String nomeLista;
    private Boolean privada;
    private LocalDate dataCriacao;

}
