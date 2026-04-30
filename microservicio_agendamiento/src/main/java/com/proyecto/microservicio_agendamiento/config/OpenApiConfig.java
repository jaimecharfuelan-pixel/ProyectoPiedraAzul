package com.proyecto.microservicio_agendamiento.config;

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
                        .title("MS Agendamiento API")
                        .description("Microservicio de gestión de citas médicas — Clínica Piedra Azul. " +
                                "Permite agendar, reagendar, cancelar y consultar disponibilidad de citas.")
                        .version("1.0.0"));
    }
}
