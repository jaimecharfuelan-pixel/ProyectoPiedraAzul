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
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/auth/login", "/api/auth/validar", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/pacientes").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/personas").hasAuthority("Administrador")
                    .requestMatchers(HttpMethod.POST, "/api/personas").hasAuthority("Administrador")
                    .requestMatchers(HttpMethod.PUT, "/api/personas/**").hasAuthority("Administrador")
                    .requestMatchers(HttpMethod.DELETE, "/api/personas/**").hasAuthority("Administrador")
                    .requestMatchers(HttpMethod.GET, "/api/usuarios").hasAuthority("Administrador")
                    .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasAuthority("Administrador")
                    .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasAuthority("Administrador")
                    .requestMatchers(HttpMethod.PUT, "/api/medicos/**/especialidad").hasAuthority("Administrador")
                    .requestMatchers("/api/roles/**").hasAuthority("Administrador")
                    .anyRequest().authenticated()
                .and()
                .httpBasic().disable()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
