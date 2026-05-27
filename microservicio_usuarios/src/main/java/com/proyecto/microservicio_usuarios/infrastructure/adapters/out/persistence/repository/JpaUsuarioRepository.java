package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.repository;

import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {
    Optional<UsuarioEntity> findByUsuario(String usuario);
    boolean existsByUsuario(String usuario);
}
