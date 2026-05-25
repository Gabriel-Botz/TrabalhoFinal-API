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
@Schema(description = "DTO para cadastro de avalaiação de filme")
public class AvaliacaoFilmeRequestDTO {

    @NotNull(message = "A nota é obrigátoria")
    @DecimalMin(value = "0.0", message = "A nota mínima é 0")
    @DecimalMax(value = "10.0", message = "A nota maxima é 10")
    @Schema(description = "Nota da avaliação", example = "8.5", minimum = "0", maximum = "10")
    private Double nota;

    @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
    @Schema(description = "Comentario sobre o filme",example = "Filme excelente, ótima atuação e roteiro.")
    private String comentario;

    @NotNull(message = "O ID do usuário é obrigatório")
    @Schema(description = "ID do usuário que realizou a avaliação", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID usuarioId;

    @NotNull(message = "O ID do filme é obrigatório")
    @Schema(description = "ID do filme avaliado", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID filmeId;
}
