package org.serratec.TrabalhoFinal_API.exception;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Estrutura padrão de retorno para erros e exceções da API")
public class ErroResposta {

    @Schema(description = "Código do status HTTP", example = "404")
    private Integer status;

    @Schema(description = "Mensagem amigável sobre o erro", example = "Recurso não encontrado!")
    private String titulo;

    @Schema(description = "Data e hora em que o erro ocorreu")
    private LocalDateTime dataHora;

    @Schema(description = "Lista detalhada de cada erro encontrado")
    private List<String> erros;

}
