package com.machry.machryapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI machryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Machry API")
                        .version("v1")
                        .description("API de itens, lotes, fichas técnicas e processamentos do Machry."));
    }
}
