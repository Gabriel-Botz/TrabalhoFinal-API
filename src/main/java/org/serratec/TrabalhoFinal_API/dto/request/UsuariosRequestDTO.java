package org.serratec.TrabalhoFinal_API.dto.request;

import java.time.LocalDateTime;

import org.serratec.TrabalhoFinal_API.domain.Usuarios;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

@Schema(description = "Modelo de requisição para cadastro e atualização de usuario")
public record UsuariosRequestDTO(
        @Schema(description = "Nome do usuario", example = "Luna", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "O campo não pode estar em branco") String nome,

        @Schema(description = "Endereço de e-mail principal", example = "marcia.silva@email.com", requiredMode = Schema.RequiredMode.REQUIRED) @Email(message = "Email inválido!") @NotBlank(message = "O campo EMAIL não pode estar em branco") String email,

        @Schema(description = "Login do usuário", example = "LunaPhonPhon", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "O campo não pode estar em branco") String username,

        @Schema(description = "Senha do usuario cadastrado", example = "********", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "O campo não pode estar em branco") String senha,

        @Schema(description = "Data e hora em que o pedido de adoção foi feito", example = "2026-05-21T15:00:00", requiredMode = Schema.RequiredMode.REQUIRED) @PastOrPresent(message = "Data de pedido inválido.") LocalDateTime dataCriacao,

        String fotoPerfil

) {

    public Usuarios toUsuario() {
        Usuarios usuario = new Usuarios();

        usuario.setNome(this.nome());
        usuario.setEmail(this.email());
        usuario.setUsername(this.username());
        usuario.setSenha(this.senha());
        usuario.setFotoPerfil(this.fotoPerfil());

        return usuario;
    }
}
