package com.proyecto.microservicio_configuracion.domain.ports.out;

import com.proyecto.microservicio_configuracion.domain.model.Especialidad;

import java.util.List;
import java.util.Optional;

public interface EspecialidadRepositoryPort {
    List<Especialidad> findAll();
    Optional<Especialidad> findById(int id);
}
