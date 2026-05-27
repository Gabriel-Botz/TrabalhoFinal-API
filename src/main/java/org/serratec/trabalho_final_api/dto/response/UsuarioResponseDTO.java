package org.serratec.trabalho_final_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.enumerated.TipoUsuario;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Schema(description = "Modelo de resposta com os dados detalhados do usuário")
@JsonPropertyOrder({ "id", "username", "nome", "email", "dataCriacao" })
public record UsuarioResponseDTO(
        @Schema(description = "Identificador único gerado automaticamente pelo banco de dados", example = "1") UUID id,
        @Schema(description = "Nome do usuario cadastrado", example = "Luna") String nome,
        @Schema(description = "Endereço de e-mail cadastrado", example = "marcia.silva@email.com") String email,
        @Schema(description = "Login do usuario cadastrado", example = "LinaPhon") String username,
        @Schema(description = "Tipo de usurio cadastrado", example = "ADMIN") @Enumerated(EnumType.STRING) TipoUsuario tipoUsuario,
        @Schema(description = "Data e Horário da criação da conta", example = "13-03-2026T00:00") LocalDateTime dataCriacao,
        @Schema(description = "Endereço de imagem cadastrado", example = "localhost://8080/mainha.png") String fotoPerfil) {

    public static UsuarioResponseDTO toUsuarioResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getUsername(),
                usuario.getTipoUsuario(),
                usuario.getDataCriacao(),
                usuario.getFotoPerfil());
    }
}
