package com.proyecto.microservicio_usuarios.domain.ports.out;

import com.proyecto.microservicio_usuarios.domain.model.Rol;

import java.util.List;
import java.util.Optional;

public interface RolRepositoryPort {
    Rol save(Rol rol);
    List<Rol> findAll();
    List<Rol> findByIdUsuario(int idUsuario);
    Optional<Rol> findFirstByIdUsuario(int idUsuario);
    boolean existsById(int id);
    void deleteById(int id);
}
