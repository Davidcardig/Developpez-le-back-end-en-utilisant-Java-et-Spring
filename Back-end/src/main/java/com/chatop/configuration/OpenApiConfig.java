package com.chatop.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI chatopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chatop API")
                        .description("API de gestion des locations de logements entre particuliers")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("David Cardigos")
                                .email("davidcardigos@hotmail.fr")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token d'authentification")));
    }
}

