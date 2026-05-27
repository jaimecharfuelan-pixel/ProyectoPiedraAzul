package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.token;

import com.proyecto.microservicio_usuarios.domain.ports.out.TokenGeneratorPort;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Base64TokenGeneratorAdapter implements TokenGeneratorPort {

    @Override
    public String generarToken(int idUsuario, String nombreUsuario) {
        String payload = idUsuario + ":" + nombreUsuario + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }
}
