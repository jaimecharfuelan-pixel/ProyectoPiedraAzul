package com.proyecto.logica.servicios;

import com.proyecto.logica.modelos.Usuario;
import com.proyecto.logica.modelos.SesionToken;
import com.proyecto.persistencia.repositorios.RepositorioSesionToken;
import com.proyecto.seguridad.JwtUtil;

import java.time.LocalDateTime;

public class ServicioAuth {

    private ServicioUsuarios servicioUsuarios;
    private RepositorioSesionToken repoToken;

    public ServicioAuth(ServicioUsuarios servicioUsuarios) {
        this.servicioUsuarios = servicioUsuarios;
        this.repoToken = new RepositorioSesionToken();
    }

    public String login(String user, String pass) {

        Usuario usuario = servicioUsuarios.autenticar(user, pass);

        if (usuario == null)
            return null;

        // Generar JWT
        String token = JwtUtil.generarToken(
                usuario.getIdUsuario(),
                usuario.getUsuario());

        // Guardar en BD
        SesionToken sesion = new SesionToken();
        sesion.setTokenHash(token);
        sesion.setFechaCreacion(LocalDateTime.now());
        sesion.setFechaExpiracion(LocalDateTime.now().plusHours(1));
        sesion.setIdEstado(2); // activo
        sesion.setIdUsuario(usuario.getIdUsuario());

        repoToken.guardar(sesion);

        return token;
    }
}