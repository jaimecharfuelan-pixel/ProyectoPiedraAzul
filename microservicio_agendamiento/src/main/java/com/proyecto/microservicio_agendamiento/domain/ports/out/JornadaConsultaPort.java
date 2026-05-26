package com.proyecto.microservicio_agendamiento.domain.ports.out;

import com.proyecto.microservicio_agendamiento.application.dto.JornadaResumen;

import java.util.List;

/**
 * Puerto de salida para consultar jornadas laborales de médicos.
 * El dominio no sabe que la fuente es ms-configuracion via HTTP.
 */
public interface JornadaConsultaPort {
    List<JornadaResumen> obtenerJornadasPorMedico(int idMedico);
}
