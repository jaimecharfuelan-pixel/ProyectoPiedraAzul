package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.token;

import com.proyecto.microservicio_usuarios.domain.ports.out.TokenGeneratorPort;
import com.proyecto.microservicio_usuarios.infrastructure.security.JwtTokenProvider;
import org.springframework.stereotype.Component;

@Component
public class Base64TokenGeneratorAdapter implements TokenGeneratorPort {

    private final JwtTokenProvider jwtTokenProvider;

    public Base64TokenGeneratorAdapter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String generarToken(int idUsuario, String nombreUsuario, String rol) {
        return jwtTokenProvider.generarToken(idUsuario, nombreUsuario, rol);
    }
}
