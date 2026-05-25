package org.serratec.trabalho_final_api.enumerated;

import org.serratec.trabalho_final_api.exception.EnumValidationException;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoUsuario {
    USER, ADMIN;

    @JsonCreator
    public static TipoUsuario verificar(String value) throws EnumValidationException {
        if (value == null || value.isBlank())
            throw new EnumValidationException(
                    "O tipo de usuário não pode ser vazio! Valores permitidos: USER ou ADMIN");

        for (TipoUsuario usuario : values()) {
            if (usuario.name().equalsIgnoreCase(value))
                return usuario;
        }
        throw new EnumValidationException("Tipo de Usuário inválido! Valores permitidos: USER OU ADMIN");
    }
}
