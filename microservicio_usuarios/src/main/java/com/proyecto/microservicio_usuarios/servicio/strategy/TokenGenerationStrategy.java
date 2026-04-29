package com.proyecto.microservicio_usuarios.servicio.strategy;

import com.proyecto.microservicio_usuarios.modelo.Usuario;

public interface TokenGenerationStrategy {
    String generarToken(Usuario usuario);
}
