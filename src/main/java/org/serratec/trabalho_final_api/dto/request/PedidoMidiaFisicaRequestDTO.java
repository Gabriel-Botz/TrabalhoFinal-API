package org.serratec.trabalho_final_api.dto.request;

import java.util.UUID;

import org.serratec.trabalho_final_api.enumerated.FormatoMidia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PedidoMidiaFisicaRequestDTO {
    
    @NotNull(message = "ERRO! O ID do usuário é obrigatório!")
    private UUID usuarioId;

    @NotNull(message = "ERRO! O ID do filme é obrigatório!")
    private UUID filmeId;

    @NotNull(message = "ERRO! O Campo formato não pode ser nulo!")
    private FormatoMidia formato;

    @NotBlank(message = "ERRO! O Campo endereço não pode ficar vazio!")
    @Size(max=255, message = "ERRO! O endereço pode ter no máximo 255 caracteres!")
    private String enderecoEntrega;

    //Construtor Vazio ↓
    public PedidoMidiaFisicaRequestDTO() {
    }

    //Construtor Lotado ↓
    public PedidoMidiaFisicaRequestDTO(UUID usuarioId, UUID filmeId, FormatoMidia formato, String enderecoEntrega) {
        this.usuarioId = usuarioId;
        this.filmeId = filmeId;
        this.formato = formato;
        this.enderecoEntrega = enderecoEntrega;
    }


    //Getters e Setters ↓   
    public UUID getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }
    public UUID getFilmeId() {
        return filmeId;
    }
    public void setFilmeId(UUID filmeId) {
        this.filmeId = filmeId;
    }
    public FormatoMidia getFormato() {
        return formato;
    }
    public void setFormato(FormatoMidia formato) {
        this.formato = formato;
    }
    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }
    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }
}