package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.repository;

import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaRolRepository extends JpaRepository<RolEntity, Integer> {
    List<RolEntity> findByIdUsuario(int idUsuario);
    Optional<RolEntity> findFirstByIdUsuario(int idUsuario);
}
