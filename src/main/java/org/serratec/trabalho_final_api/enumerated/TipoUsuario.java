package org.serratec.trabalho_final_api.enumerated;

import org.serratec.trabalho_final_api.exception.EnumValidationException;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoUsuario {
    USER, ADMIN;

    @JsonCreator
    public static TipoUsuario verificar(String value) throws EnumValidationException {
        if (value.isBlank())
            return null;

        for (TipoUsuario usuario : values()) {
            if (usuario.name().equalsIgnoreCase(value))
                return usuario;
        }
        throw new EnumValidationException("Tipo de Usuaário inválido! Valores permitidos: USER OU ADMIN");
    }
}
