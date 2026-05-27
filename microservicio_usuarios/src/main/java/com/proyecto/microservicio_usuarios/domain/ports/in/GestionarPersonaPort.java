package com.proyecto.microservicio_usuarios.domain.ports.in;

import com.proyecto.microservicio_usuarios.application.dto.CrearPersonaCommand;
import com.proyecto.microservicio_usuarios.domain.model.Persona;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GestionarPersonaPort {
    Persona crearAdmin(CrearPersonaCommand command);
    boolean editarCampos(int id, Map<String, Object> campos);
    boolean inactivar(int id);
    Optional<Persona> buscarPorId(int id);
    Optional<Persona> buscarPorDocumento(String cedula);
    List<Persona> listar();
}
