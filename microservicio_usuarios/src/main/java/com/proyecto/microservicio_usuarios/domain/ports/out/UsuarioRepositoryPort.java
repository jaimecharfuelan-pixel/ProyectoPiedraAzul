package com.proyecto.microservicio_usuarios.domain.ports.out;

import com.proyecto.microservicio_usuarios.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    boolean existsByNombreUsuario(String nombreUsuario);
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(int id);
    boolean existsById(int id);
    void deleteById(int id);
    List<Usuario> findAll();
}
