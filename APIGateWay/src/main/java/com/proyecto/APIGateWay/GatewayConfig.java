package com.proyecto.APIGateWay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class GatewayConfig {

    @Value("${ms.usuarios.url:http://microservicio-usuarios:8080}")
    private String msUsuariosUrl;

    @Value("${ms.agendamiento.url:http://microservicio-agendamiento:8080}")
    private String msAgendamientoUrl;

    @Value("${ms.configuracion.url:http://microservicio-configuracion:8080}")
    private String msConfiguracionUrl;

    // ── Autenticación ──────────────────────────────────────────────────────────
    @Bean
    public RouterFunction<ServerResponse> routeAuth() {
        return GatewayRouterFunctions.route("ms-auth")
                .route(RequestPredicates.path("/api/auth/**"),
                        HandlerFunctions.http(msUsuariosUrl))
                .build();
    }

    // ── Usuarios ───────────────────────────────────────────────────────────────
    @Bean
    public RouterFunction<ServerResponse> routeUsuarios() {
        return GatewayRouterFunctions.route("ms-usuarios")
                .route(RequestPredicates.path("/api/usuarios/**"),
                        HandlerFunctions.http(msUsuariosUrl))
                .build();
    }

    // ── Pacientes ──────────────────────────────────────────────────────────────
    @Bean
    public RouterFunction<ServerResponse> routePacientes() {
        return GatewayRouterFunctions.route("ms-pacientes")
                .route(RequestPredicates.path("/api/pacientes/**"),
                        HandlerFunctions.http(msUsuariosUrl))
                .build();
    }

    // ── Médicos ────────────────────────────────────────────────────────────────
    @Bean
    public RouterFunction<ServerResponse> routeMedicos() {
        return GatewayRouterFunctions.route("ms-medicos")
                .route(RequestPredicates.path("/api/medicos/**"),
                        HandlerFunctions.http(msUsuariosUrl))
                .build();
    }

    // ── Personas ───────────────────────────────────────────────────────────────
    @Bean
    public RouterFunction<ServerResponse> routePersonas() {
        return GatewayRouterFunctions.route("ms-personas")
                .route(RequestPredicates.path("/api/personas/**"),
                        HandlerFunctions.http(msUsuariosUrl))
                .build();
    }

    // ── Citas (Agendamiento) ───────────────────────────────────────────────────
    @Bean
    public RouterFunction<ServerResponse> routeCitas() {
        return GatewayRouterFunctions.route("ms-citas")
                .route(RequestPredicates.path("/api/citas/**"),
                        HandlerFunctions.http(msAgendamientoUrl))
                .build();
    }

    // ── Jornadas (Configuración) ───────────────────────────────────────────────
    @Bean
    public RouterFunction<ServerResponse> routeJornadas() {
        return GatewayRouterFunctions.route("ms-jornadas")
                .route(RequestPredicates.path("/api/jornadas/**"),
                        HandlerFunctions.http(msConfiguracionUrl))
                .build();
    }

    // ── Especialidades (Configuración) ─────────────────────────────────────────
    @Bean
    public RouterFunction<ServerResponse> routeEspecialidades() {
        return GatewayRouterFunctions.route("ms-especialidades")
                .route(RequestPredicates.path("/api/especialidades/**"),
                        HandlerFunctions.http(msConfiguracionUrl))
                .build();
    }

    // ── Configuración general ──────────────────────────────────────────────────
    @Bean
    public RouterFunction<ServerResponse> routeConfiguracion() {
        return GatewayRouterFunctions.route("ms-configuracion")
                .route(RequestPredicates.path("/api/configuracion/**"),
                        HandlerFunctions.http(msConfiguracionUrl))
                .build();
    }
}
