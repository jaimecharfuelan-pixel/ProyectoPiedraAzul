package com.proyecto.microservicio_usuarios.domain.ports.out;

public interface TokenGeneratorPort {
    String generarToken(int idUsuario, String nombreUsuario);
}
