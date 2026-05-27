package com.proyecto.microservicio_usuarios.domain.ports.in;

import com.proyecto.microservicio_usuarios.domain.model.MedicoTerapista;

import java.util.List;

public interface GestionarMedicoPort {
    List<MedicoTerapista> listarActivos();
    boolean asignarEspecialidad(int idMedico, int idEspecialidad);
}
