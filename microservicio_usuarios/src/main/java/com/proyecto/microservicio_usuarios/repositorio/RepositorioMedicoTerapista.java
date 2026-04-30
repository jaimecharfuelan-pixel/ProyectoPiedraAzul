package com.proyecto.microservicio_usuarios.repositorio;

import com.proyecto.microservicio_usuarios.modelo.MedicoTerapista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepositorioMedicoTerapista extends JpaRepository<MedicoTerapista, Integer> {

    List<MedicoTerapista> findByIdEstado(int idEstado);
}
