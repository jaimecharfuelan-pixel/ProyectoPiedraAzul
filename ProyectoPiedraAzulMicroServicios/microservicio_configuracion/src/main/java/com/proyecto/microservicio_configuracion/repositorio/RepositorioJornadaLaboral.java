package com.proyecto.microservicio_configuracion.repositorio;

import com.proyecto.microservicio_configuracion.modelo.JornadaLaboral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepositorioJornadaLaboral extends JpaRepository<JornadaLaboral, Integer> {

    List<JornadaLaboral> findByIdUsuario(int idUsuario);

    List<JornadaLaboral> findByIdUsuarioAndDiaSemana(int idUsuario, String diaSemana);
}
