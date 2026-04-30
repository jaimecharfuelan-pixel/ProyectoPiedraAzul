package com.proyecto.microservicio_usuarios.repositorio;

import com.proyecto.microservicio_usuarios.modelo.SesionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioSesionToken extends JpaRepository<SesionToken, Integer> {
}
