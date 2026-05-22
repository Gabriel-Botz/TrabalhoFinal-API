package org.serratec.TrabalhoFinal_API.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.serratec.TrabalhoFinal_API.domain.ClassificacaoIndicativa;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "DTO para resposta de filme")
public class FilmeResponseDTO {

    @Schema(description = "Titulo do filme", example = "O Poderoso Chefão")
    private String titulo;

    @Schema(description = "Descrição do filme", example = "Um drama épico sobre uma família mafiosa")
    private String descricao;

    @Schema(description = "Duração do filme em minutos")
    private Integer duracao;

    @Schema(description = "Data de lançamento do filme")
    private LocalDate dataLancamento;

    @Schema(description = "Nota média do filme")
    private Double notaMedia;

    @Schema(description = "Classificação indicativa do filme")
    @Enumerated(EnumType.STRING)
    private ClassificacaoIndicativa classificacaoIndicativa;


}
