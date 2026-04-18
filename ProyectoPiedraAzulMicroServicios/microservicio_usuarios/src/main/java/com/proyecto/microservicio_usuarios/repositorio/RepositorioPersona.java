package com.proyecto.microservicio_usuarios.repositorio;

import com.proyecto.microservicio_usuarios.modelo.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioPersona extends JpaRepository<Persona, Integer> {

    Optional<Persona> findByCedulaCiudadania(String cedulaCiudadania);

    List<Persona> findByIdEstado(int idEstado);
}
