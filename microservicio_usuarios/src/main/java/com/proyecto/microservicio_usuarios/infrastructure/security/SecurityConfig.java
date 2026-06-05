package com.proyecto.microservicio_usuarios.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/auth/validar").permitAll()
                    // Registro de paciente: público (sin sesión)
                    .requestMatchers(HttpMethod.POST, "/api/pacientes", "/api/pacientes/").permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    // Lectura de pacientes: cualquier usuario autenticado (Médico, Agendador, Admin, Paciente)
                    .requestMatchers(HttpMethod.GET, "/api/pacientes", "/api/pacientes/**").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/personas").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/personas").hasAuthority("Administrador")
                    .requestMatchers(HttpMethod.PUT, "/api/personas/**").hasAuthority("Administrador")
                    .requestMatchers(HttpMethod.DELETE, "/api/personas/**").hasAuthority("Administrador")
                    .requestMatchers(HttpMethod.GET, "/api/usuarios").hasAuthority("Administrador")
                    .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasAuthority("Administrador")
                    .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasAuthority("Administrador")
                    .requestMatchers(HttpMethod.PUT, "/api/medicos/*/especialidad").hasAuthority("Administrador")
                    .requestMatchers("/api/roles/**").hasAuthority("Administrador")
                    .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
