package org.serratec.TrabalhoFinal_API.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.serratec.TrabalhoFinal_API.domain.Usuarios;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo de resposta com os dados detalhados do usuário")
@JsonPropertyOrder({ "id", "nome", "email", "username", "dataCriacao" })
public record UsuariosResponseDTO(
        @Schema(description = "Identificador único gerado automaticamente pelo banco de dados", example = "1") UUID id,
        @Schema(description = "Nome do usuario cadastrado", example = "Luna") String nome,
        @Schema(description = "Endereço de e-mail cadastrado", example = "marcia.silva@email.com") String email,
        @Schema(description = "Login do usuario cadastrado", example = "LinaPhon") String username,
        @Schema(description = "Endereço de e-mail cadastrado", example = "marcia.silva@email.com") LocalDateTime dataCriacao,
        String fotoPerfil) {

    public static UsuariosResponseDTO toUsuarioResponseDTO(Usuarios usuario) {
        return new UsuariosResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getUsername(),
                usuario.getDataCriacao(),
                usuario.getFotoPerfil());
    }
}
