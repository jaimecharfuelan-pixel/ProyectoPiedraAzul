package com.proyecto.microservicio_usuarios.infrastructure.security;

import com.proyecto.microservicio_usuarios.domain.ports.in.AutenticarUsuarioPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AutenticarUsuarioPort autenticarUsuario;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   AutenticarUsuarioPort autenticarUsuario) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.autenticarUsuario = autenticarUsuario;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path   = request.getRequestURI();
        String method = request.getMethod();

        if (isPermittedPath(path, method) || "OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Se requiere un token Bearer válido.");
            return;
        }

        String token = header.substring(7);
        if (!jwtTokenProvider.validarToken(token) || !autenticarUsuario.validarToken(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado.");
            return;
        }

        String username = jwtTokenProvider.obtenerNombreUsuario(token);
        String rol = jwtTokenProvider.obtenerRol(token);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                rol != null ? List.of(new SimpleGrantedAuthority(rol)) : List.of()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    /**
     * Rutas que no requieren token JWT.
     * Solo el POST a /api/pacientes (registro público) es libre;
     * el GET requiere autenticación y se gestiona en SecurityConfig.
     */
    private boolean isPermittedPath(String path, String method) {
        if (path.equals("/api/auth/login"))  return true;
        if (path.equals("/api/auth/validar")) return true;
        if (path.startsWith("/swagger-ui"))  return true;
        if (path.startsWith("/v3/api-docs")) return true;
        // Solo el POST de registro de paciente es público (sin sesión)
        if ((path.equals("/api/pacientes") || path.equals("/api/pacientes/"))
                && "POST".equalsIgnoreCase(method)) return true;
        return false;
    }
}
