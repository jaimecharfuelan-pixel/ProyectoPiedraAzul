package com.proyecto.microservicio_configuracion.domain.ports.in;

import com.proyecto.microservicio_configuracion.domain.model.MedicoResumen;

import java.util.List;

public interface ConsultarMedicosPort {
    List<MedicoResumen> listarActivos();
}
