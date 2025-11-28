package com.tennistournament.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tennisTournamentOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tennis Tournament Management API")
                        .description("REST API for managing tennis tournaments, training sessions, clubs, and user profiles")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tennis Tournament Team")
                                .email("support@tennistournament.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}

