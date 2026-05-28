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
                contato.setName("Grupo 02");
                contato.setUrl("https://github.com/");

                License apacheLicense = new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0");

                Info info = new Info()
                                .title("title")
                                .description("description")
                                .version("1.0.0")
                                .contact(contato)
                                .license(apacheLicense);

                Server localServer = new Server()
                                .url("http://localhost:8081")
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