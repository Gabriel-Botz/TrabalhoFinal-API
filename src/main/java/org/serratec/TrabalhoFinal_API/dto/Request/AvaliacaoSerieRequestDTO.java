package org.serratec.TrabalhoFinal_API.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvaliacaoSerieRequestDTO {

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    private Double nota;

    @Size
    private String comentario;

    @NotNull
    private UUID usuarioId;

    @NotNull
    private UUID serieId;
}
