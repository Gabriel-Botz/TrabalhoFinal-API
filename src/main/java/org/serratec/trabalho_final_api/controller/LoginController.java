package org.serratec.trabalho_final_api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController // GAMBIARRA a gente aceita, só não aceita a derrota!
@Tag(name = "Autenticação", description = "Endpoint para login de usuários")
public class LoginController {

    @PostMapping("/login")
    @Operation(summary = "Realiza o login do usuário", description = "Informe o username e a senha para receber o token JWT no cabeçalho e no corpo da resposta.", responses = {
            @ApiResponse(responseCode = "200", description = "Login efetuado com sucesso", content = @Content(schema = @Schema(implementation = LoginResponseSchema.class))),
            @ApiResponse(responseCode = "401", description = "Usuário ou senha inválidos")
    })
    public void login(@RequestBody LoginRequestSchema loginRequest) {
    }

    private static class LoginRequestSchema {
        @io.swagger.v3.oas.annotations.media.Schema(example = "usuario_teste")
        private String username;
        @io.swagger.v3.oas.annotations.media.Schema(example = "senha123")
        private String senha;

        public String getUsername() {
            return username;
        }

        public String getSenha() {
            return senha;
        }
    }

    private static class LoginResponseSchema {
        @io.swagger.v3.oas.annotations.media.Schema(example = "Bearer eyJhbGciOiJIUzUxMiJ9...")
        private String token;

        public String getToken() {
            return token;
        }
    }
}