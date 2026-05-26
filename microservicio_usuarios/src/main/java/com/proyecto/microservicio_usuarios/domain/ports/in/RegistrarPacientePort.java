package com.proyecto.microservicio_usuarios.domain.ports.in;

import com.proyecto.microservicio_usuarios.application.dto.RegistrarPacienteCommand;
import com.proyecto.microservicio_usuarios.domain.model.Paciente;

import java.util.List;
import java.util.Optional;

public interface RegistrarPacientePort {
    Paciente registrar(RegistrarPacienteCommand command);
    List<Paciente> listar();
    Optional<Paciente> buscarPorId(int id);
}
