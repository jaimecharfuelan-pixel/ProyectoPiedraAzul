package com.proyecto.microservicio_usuarios.servicio;

import com.proyecto.microservicio_usuarios.modelo.Usuario;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioUsuario;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicioUsuarios {

    private final RepositorioUsuario repoUsuario;

    public ServicioUsuarios(RepositorioUsuario repoUsuario) {
        this.repoUsuario = repoUsuario;
    }

    public Usuario autenticar(String nombreUsuario, String contrasena) {
        return repoUsuario.findByUsuario(nombreUsuario)
                .filter(u -> u.getContrasena().equals(contrasena))
                .orElse(null);
    }

    public boolean registrarUsuario(Usuario usuario) {
        if (usuario.getUsuario() == null || usuario.getUsuario().isEmpty()) return false;
        repoUsuario.save(usuario);
        return true;
    }

    public boolean editarUsuario(Usuario usuario) {
        if (!repoUsuario.existsById(usuario.getIdUsuario())) return false;
        repoUsuario.save(usuario);
        return true;
    }

    public boolean eliminarUsuario(int idUsuario) {
        if (!repoUsuario.existsById(idUsuario)) return false;
        repoUsuario.deleteById(idUsuario);
        return true;
    }

    public List<Usuario> listarUsuarios() {
        return repoUsuario.findAll();
    }
}
