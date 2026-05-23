package org.serratec.trabalho_final_api.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @RestControllerAdvice
    public static class GlobalExceptionHandler {

        @ExceptionHandler(RecursoNaoEncontradoException.class)
        public ResponseEntity<Map<String, Object>> handleNotFound(RecursoNaoEncontradoException ex) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("status", 404);
            erro.put("mensagem", ex.getMessage());
            erro.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
        }

        @ExceptionHandler(EnumValidationException.ConflictException.class)
        public ResponseEntity<Map<String, Object>> handleConflict(EnumValidationException.ConflictException ex) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("status", 409);
            erro.put("mensagem", ex.getMessage());
            erro.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("status", 500);
            erro.put("mensagem", "Erro interno no servidor");
            erro.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

    public static class RecursoNaoEncontradoException extends RuntimeException {
        public RecursoNaoEncontradoException(String mensagem) {
            super(mensagem);
        }
    }
}
