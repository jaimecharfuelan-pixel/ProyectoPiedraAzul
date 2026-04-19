package com.proyecto.microservicio_configuracion.repositorio;

import com.proyecto.microservicio_configuracion.modelo.DominioEspecialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioDominioEspecialidad extends JpaRepository<DominioEspecialidad, Integer> {
}
