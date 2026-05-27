package com.proyecto.microservicio_configuracion.infrastructure.adapters.out.persistence.repository;

import com.proyecto.microservicio_configuracion.infrastructure.adapters.out.persistence.entity.JornadaLaboralEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaJornadaRepository extends JpaRepository<JornadaLaboralEntity, Integer> {
    List<JornadaLaboralEntity> findByIdUsuario(int idUsuario);
    List<JornadaLaboralEntity> findByIdUsuarioAndDiaSemana(int idUsuario, String diaSemana);
}
