package org.serratec.TrabalhoFinal_API.exception;

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
