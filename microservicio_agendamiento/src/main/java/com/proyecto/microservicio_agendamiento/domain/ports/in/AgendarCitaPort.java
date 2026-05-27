package com.proyecto.microservicio_agendamiento.domain.ports.in;

import com.proyecto.microservicio_agendamiento.application.dto.AgendarCitaWebCommand;
import com.proyecto.microservicio_agendamiento.application.dto.CrearCitaManualCommand;
import com.proyecto.microservicio_agendamiento.domain.model.Cita;

public interface AgendarCitaPort {
    boolean agendarWeb(AgendarCitaWebCommand command);
    boolean crearManual(CrearCitaManualCommand command);
}
