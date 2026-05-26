package com.proyecto.microservicio_agendamiento.domain.ports.in;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ConsultarDisponibilidadPort {
    List<LocalTime> consultar(int idMedico, LocalDate fecha);
}
