package com.proyecto.APIGateWay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Configuración de rutas del API Gateway.
 *
 * Cada bean agrupa todas las rutas que van al mismo microservicio destino,
 * evitando un @Bean por cada path individual.
 *
 * Rutas:
 *   /api/auth/**           → ms-usuarios
 *   /api/usuarios/**       → ms-usuarios
 *   /api/pacientes/**      → ms-usuarios
 *   /api/medicos/**        → ms-usuarios
 *   /api/personas/**       → ms-usuarios
 *   /api/roles/**          → ms-usuarios
 *   /api/citas/**          → ms-agendamiento
 *   /api/jornadas/**       → ms-configuracion
 *   /api/especialidades/** → ms-configuracion
 *   /api/configuracion/**  → ms-configuracion
 */
@Configuration
public class GatewayConfig {

    @Value("${ms.usuarios.url:http://microservicio-usuarios:8080}")
    private String msUsuariosUrl;

    @Value("${ms.agendamiento.url:http://microservicio-agendamiento:8080}")
    private String msAgendamientoUrl;

    @Value("${ms.configuracion.url:http://microservicio-configuracion:8080}")
    private String msConfiguracionUrl;

    /**
     * Rutas hacia ms-usuarios:
     * auth, usuarios, pacientes, medicos, personas, roles.
     */
    @Bean
    public RouterFunction<ServerResponse> routesMsUsuarios() {
        return GatewayRouterFunctions.route("ms-usuarios")
                .route(RequestPredicates.path("/api/auth/**")
                        .or(RequestPredicates.path("/api/usuarios/**"))
                        .or(RequestPredicates.path("/api/pacientes"))
                        .or(RequestPredicates.path("/api/pacientes/"))
                        .or(RequestPredicates.path("/api/pacientes/**"))
                        .or(RequestPredicates.path("/api/medicos/**"))
                        .or(RequestPredicates.path("/api/personas/**"))
                        .or(RequestPredicates.path("/api/roles/**")),
                        HandlerFunctions.http(msUsuariosUrl))
                .build();
    }

    /**
     * Rutas hacia ms-agendamiento:
     * citas.
     */
    @Bean
    public RouterFunction<ServerResponse> routesMsAgendamiento() {
        return GatewayRouterFunctions.route("ms-agendamiento")
                .route(RequestPredicates.path("/api/citas/**"),
                        HandlerFunctions.http(msAgendamientoUrl))
                .build();
    }

    /**
     * Rutas hacia ms-configuracion:
     * jornadas, especialidades, configuracion.
     */
    @Bean
    public RouterFunction<ServerResponse> routesMsConfiguracion() {
        return GatewayRouterFunctions.route("ms-configuracion")
                .route(RequestPredicates.path("/api/jornadas/**")
                        .or(RequestPredicates.path("/api/especialidades/**"))
                        .or(RequestPredicates.path("/api/configuracion/**")),
                        HandlerFunctions.http(msConfiguracionUrl))
                .build();
    }

    /**
     * Health check propio del gateway.
     * GET /gateway/health → 200 OK con estado del gateway.
     */
    @Bean
    public RouterFunction<ServerResponse> gatewayHealth() {
        return RouterFunctions.route(
                RequestPredicates.GET("/gateway/health"),
                request -> ServerResponse.ok().body("API Gateway operativo"));
    }
}
