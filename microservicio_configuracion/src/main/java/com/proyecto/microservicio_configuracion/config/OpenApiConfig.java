package com.proyecto.microservicio_configuracion.config;

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
                        .title("MS Configuracion API")
                        .description("Microservicio de configuración — Clínica Piedra Azul. " +
                                "Gestiona jornadas laborales de médicos y especialidades médicas.")
                        .version("1.0.0"));
    }
}
