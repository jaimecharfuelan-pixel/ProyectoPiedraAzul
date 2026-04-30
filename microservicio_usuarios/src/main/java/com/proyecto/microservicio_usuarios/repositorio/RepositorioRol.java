package com.proyecto.microservicio_usuarios.repositorio;

import com.proyecto.microservicio_usuarios.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioRol extends JpaRepository<Rol, Integer> {

    List<Rol> findByIdUsuario(int idUsuario);

    Optional<Rol> findFirstByIdUsuario(int idUsuario);
}
