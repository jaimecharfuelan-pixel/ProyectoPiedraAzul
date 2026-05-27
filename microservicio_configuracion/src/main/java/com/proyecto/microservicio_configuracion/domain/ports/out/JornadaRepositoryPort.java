package com.proyecto.microservicio_configuracion.domain.ports.out;

import com.proyecto.microservicio_configuracion.domain.model.JornadaLaboral;

import java.util.List;
import java.util.Optional;

public interface JornadaRepositoryPort {
    JornadaLaboral save(JornadaLaboral jornada);
    Optional<JornadaLaboral> findById(int id);
    boolean existsById(int id);
    void deleteById(int id);
    List<JornadaLaboral> findAll();
    List<JornadaLaboral> findByMedico(int idMedico);
    List<JornadaLaboral> findByMedicoYDia(int idMedico, String diaSemana);
}
