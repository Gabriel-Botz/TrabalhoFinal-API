package org.serratec.trabalho_final_api.exception;

public class EnumValidationException extends RuntimeException {

    public EnumValidationException(String message) {
        super(message);
    }

    public static class ConflictException extends RuntimeException {
        public ConflictException(String mensagem) {
            super(mensagem);
        }
    }
}
