package com.proyecto.microservicio_usuarios.domain.ports.in;

import com.proyecto.microservicio_usuarios.application.dto.LoginCommand;
import com.proyecto.microservicio_usuarios.application.dto.LoginResult;

public interface AutenticarUsuarioPort {
    LoginResult login(LoginCommand command);
    boolean validarToken(String token);
    boolean cerrarSesion(String token);
}
