package com.proyecto.microservicio_usuarios.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS Usuarios API")
                        .description("Microservicio de gestión de usuarios, personas, pacientes y médicos — Clínica Piedra Azul.")
                        .version("2.0.0"));
    }
}
