package com.proyecto.microservicio_usuarios.domain.ports.out;

import com.proyecto.microservicio_usuarios.domain.model.Persona;

import java.util.List;
import java.util.Optional;

public interface PersonaRepositoryPort {
    Persona save(Persona persona);
    Optional<Persona> findById(int id);
    Optional<Persona> findByCedula(String cedula);
    List<Persona> findActivos();
}
