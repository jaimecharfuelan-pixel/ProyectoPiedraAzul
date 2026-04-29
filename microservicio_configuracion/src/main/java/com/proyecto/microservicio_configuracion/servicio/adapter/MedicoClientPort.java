package com.proyecto.microservicio_configuracion.servicio.adapter;

import com.proyecto.microservicio_configuracion.dto.MedicoResumenDTO;

import java.util.List;

public interface MedicoClientPort {
    List<MedicoResumenDTO> listarMedicosActivos();
}
