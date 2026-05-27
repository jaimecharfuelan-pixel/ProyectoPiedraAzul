package com.proyecto.microservicio_usuarios.domain.ports.out;

import com.proyecto.microservicio_usuarios.domain.model.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteRepositoryPort {
    Paciente save(Paciente paciente);
    Optional<Paciente> findById(int id);
    List<Paciente> findAll();
}
