package com.proyecto.microservicio_usuarios.repositorio;

import com.proyecto.microservicio_usuarios.modelo.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioPaciente extends JpaRepository<Paciente, Integer> {
}
