package org.exi.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI exiOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Gestione Utenti - EXI API")
                .description("API REST per la gestione degli utenti (CRUD) con sicurezza e ruoli.")
                .version("1.0.0")
            );
    }
}