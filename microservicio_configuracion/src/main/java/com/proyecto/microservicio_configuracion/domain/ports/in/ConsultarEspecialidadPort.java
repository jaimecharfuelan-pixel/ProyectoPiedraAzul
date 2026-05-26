package com.proyecto.microservicio_configuracion.domain.ports.in;

import com.proyecto.microservicio_configuracion.domain.model.Especialidad;

import java.util.List;
import java.util.Optional;

public interface ConsultarEspecialidadPort {
    List<Especialidad> listar();
    Optional<Especialidad> buscarPorId(int id);
}
