package org.serratec.trabalho_final_api.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

        @Bean
        public OpenAPI myOpenAPI() {

                Contact contato = new Contact();
                contato.setName("Grupo 02 - Serratec / Turma 37");
                contato.setUrl("https://github.com/Gabriel-Botz/TrabalhoFinal-API");
                contato.setEmail("romulo.lima@docente.senai.br");

                License apacheLicense = new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0");

                Info info = new Info()
                                .title("API de Streaming e Avaliação de Filmes/Séries - Serraflix")
                                .description("description")
                                .description("Esta API fornece endpoints para gerenciamento de catálogo de produções (filmes e séries), "
                                                +
                                                "criação de listas de favoritos, avaliações personalizadas e administração de usuários.")
                                .version("1.0.0")
                                .contact(contato)
                                .license(apacheLicense);

                Server localServer = new Server()
                                .url("http://localhost:8082")
                                .description("Servidor Local de Desenvolvimento");

                final String securitySchemeName = "Bearer Authentication";

                return new OpenAPI()
                                .info(info)
                                .servers(List.of(localServer))
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .components(new Components()
                                                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                                                .name(securitySchemeName)
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")));
        }

}