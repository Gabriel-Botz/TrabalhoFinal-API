package org.serratec.trabalho_final_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.serratec.trabalho_final_api.domain.ClassificacaoIndicativa;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para requisição de criação de filme")
public class FilmeRequestDTO {

    @Schema(description = "Titulo do filme", example = "O Poderoso Chefão")
    @NotBlank(message = "O campo titulo é obrigatório")
    @Size(max = 40, message = "O campo titulo deve conter no máximo 40 caracteres")
    private String titulo;

    @Schema(description = "Descrição do filme", example = "Um drama épico sobre uma família mafiosa")
    @NotBlank(message = "O campo descrição é obrigatório")
    @Size(max = 200, message = "O campo descrição deve conter no máximo 200 caracteres")
    private String descricao;


    @Schema(description = "Duração do filme em minutos")
    @NotNull(message = "O campo duração é obrigatório")
    private Integer duracao;


    @Schema(description = "Data de lançamento do filme")
    @NotNull(message = "O campo data de lancamento é obrigatório")
    private LocalDate dataLancamento;

    private ClassificacaoIndicativa classificacaoIndicativa;
}


