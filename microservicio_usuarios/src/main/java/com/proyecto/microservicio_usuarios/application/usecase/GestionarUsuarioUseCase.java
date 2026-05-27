package com.proyecto.microservicio_usuarios.application.usecase;

import com.proyecto.microservicio_usuarios.domain.model.Usuario;
import com.proyecto.microservicio_usuarios.domain.ports.in.GestionarUsuarioPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GestionarUsuarioUseCase implements GestionarUsuarioPort {

    private final UsuarioRepositoryPort usuarioRepo;

    public GestionarUsuarioUseCase(UsuarioRepositoryPort usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public boolean registrar(Usuario usuario) {
        if (usuario.getUsuario() == null || usuario.getUsuario().isBlank()) return false;
        usuarioRepo.save(usuario);
        return true;
    }

    @Override
    public boolean editar(Usuario usuario) {
        if (!usuarioRepo.existsById(usuario.getIdUsuario())) return false;
        usuarioRepo.save(usuario);
        return true;
    }

    @Override
    public boolean eliminar(int idUsuario) {
        if (!usuarioRepo.existsById(idUsuario)) return false;
        usuarioRepo.deleteById(idUsuario);
        return true;
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepo.findAll();
    }
}
