package com.proyecto.microservicio_usuarios.servicio;

import com.proyecto.microservicio_usuarios.dto.LoginResponseDTO;
import com.proyecto.microservicio_usuarios.modelo.Rol;
import com.proyecto.microservicio_usuarios.modelo.SesionToken;
import com.proyecto.microservicio_usuarios.modelo.Usuario;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioRol;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioSesionToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class ServicioAuth {

    private final ServicioUsuarios servicioUsuarios;
    private final RepositorioSesionToken repoToken;
    private final RepositorioRol repoRol;

    public ServicioAuth(ServicioUsuarios servicioUsuarios,
                        RepositorioSesionToken repoToken,
                        RepositorioRol repoRol) {
        this.servicioUsuarios = servicioUsuarios;
        this.repoToken = repoToken;
        this.repoRol = repoRol;
    }

    /**
     * Autentica al usuario, genera token de sesión y devuelve datos de login.
     * Nota: token Base64 simple — reemplazar por JWT en producción.
     */
    public LoginResponseDTO login(String nombreUsuario, String contrasena) {
        Usuario usuario = servicioUsuarios.autenticar(nombreUsuario, contrasena);
        if (usuario == null) return null;

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

        String rolNombre = repoRol.findFirstByIdUsuario(usuario.getIdUsuario())
                .map(Rol::getNombre)
                .orElse("Sin rol");

        return new LoginResponseDTO(token, usuario.getIdUsuario(), usuario.getUsuario(), rolNombre);
    }

    public boolean validarToken(String token) {
        return repoToken.findAll().stream()
                .anyMatch(t -> t.getTokenHash().equals(token)
                        && t.getIdEstado() == 2
                        && t.getFechaExpiracion().isAfter(LocalDateTime.now()));
    }

    public boolean cerrarSesion(String token) {
        return repoToken.findAll().stream()
                .filter(t -> t.getTokenHash().equals(token))
                .findFirst()
                .map(t -> {
                    t.setIdEstado(1); // inactivo
                    repoToken.save(t);
                    return true;
                }).orElse(false);
    }
}
