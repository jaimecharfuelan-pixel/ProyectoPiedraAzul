package com.proyecto.microservicio_usuarios.servicio;

import com.proyecto.microservicio_usuarios.modelo.SesionToken;
import com.proyecto.microservicio_usuarios.modelo.Usuario;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioSesionToken;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class ServicioAuth {

    private final ServicioUsuarios servicioUsuarios;
    private final RepositorioSesionToken repoToken;

    public ServicioAuth(ServicioUsuarios servicioUsuarios, RepositorioSesionToken repoToken) {
        this.servicioUsuarios = servicioUsuarios;
        this.repoToken = repoToken;
    }

    /**
     * Autentica al usuario y genera un token de sesión.
     * Nota: en producción reemplazar por JWT real (jjwt / spring-security-oauth2).
     */
    public String login(String nombreUsuario, String contrasena) {
        Usuario usuario = servicioUsuarios.autenticar(nombreUsuario, contrasena);
        if (usuario == null) return null;

        // Token simple Base64 — reemplazar por JWT en producción
        String token = Base64.getEncoder().encodeToString(
                (usuario.getIdUsuario() + ":" + usuario.getUsuario() + ":" + System.currentTimeMillis()).getBytes()
        );

        SesionToken sesion = new SesionToken();
        sesion.setTokenHash(token);
        sesion.setFechaCreacion(LocalDateTime.now());
        sesion.setFechaExpiracion(LocalDateTime.now().plusHours(1));
        sesion.setIdEstado(2); // activo
        sesion.setIdUsuario(usuario.getIdUsuario());
        repoToken.save(sesion);

        return token;
    }
}
