package com.proyecto.microservicio_configuracion.domain.ports.out;

import com.proyecto.microservicio_configuracion.domain.model.MedicoResumen;

import java.util.List;

/**
 * Puerto de salida para consultar médicos activos en ms-usuarios.
 * El dominio no sabe que la fuente es HTTP/REST.
 */
public interface MedicoClientPort {
    List<MedicoResumen> listarMedicosActivos();
}
