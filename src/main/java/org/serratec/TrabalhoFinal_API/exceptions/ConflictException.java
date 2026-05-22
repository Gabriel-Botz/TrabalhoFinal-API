package org.serratec.TrabalhoFinal_API.exceptions;

public class ConflictException extends RuntimeException {
    public ConflictException(String mensagem) {
        super(mensagem);
    }
}