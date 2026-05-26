package org.serratec.trabalho_final_api.dto.request;

import java.util.UUID;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO para cadastro de avaliação de série")
public class AvaliacaoSerieRequestDTO {

    @NotNull(message = "A nota da série e obrigatória")
    @DecimalMin(value = "0.0", message = "A nota mínima é 0")
    @DecimalMax(value = "10.0", message = "A nota maxima é 10")
    @Schema(description = "Nota da avaliação da série", example = "9.0", minimum = "0", maximum = "10")
    private Double nota;

    @Size( max = 500, message = "O comentario deve ter no maximo 500 caracteres")
    @Schema(description = "Comentário sobre a série", example = "Série muito envolvente, excelente desenvolvimento dos personagens.")
    private String comentario;

    @NotNull(message = "O ID do usuario é obrigatório")
    @Schema(description = "ID do usuario que realizou a avaliação", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID usuarioId;

    @NotNull(message = "O ID da série é obrigatorio ")
    @Schema(description = "ID da série avaliada", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID serieId;
}
