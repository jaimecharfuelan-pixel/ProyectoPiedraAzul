package com.proyecto.microservicio_usuarios.servicio.strategy;

import com.proyecto.microservicio_usuarios.modelo.Usuario;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Base64TokenGenerationStrategy implements TokenGenerationStrategy {
    @Override
    public String generarToken(Usuario usuario) {
        String payload = usuario.getIdUsuario() + ":" + usuario.getUsuario() + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }
}
