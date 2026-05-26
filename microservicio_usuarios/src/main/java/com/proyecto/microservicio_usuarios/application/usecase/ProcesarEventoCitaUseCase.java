package com.proyecto.microservicio_usuarios.application.usecase;

import com.proyecto.microservicio_usuarios.domain.ports.in.ProcesarEventoCitaPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProcesarEventoCitaUseCase implements ProcesarEventoCitaPort {

    private static final Logger log = LoggerFactory.getLogger(ProcesarEventoCitaUseCase.class);

    @Override
    public void procesarCitaCreada(int idCita, int idPaciente, int idMedico) {
        // Punto de extensión: aquí se puede notificar al paciente, registrar historial, etc.
        log.info("[CITA CREADA] idCita={} idPaciente={} idMedico={}", idCita, idPaciente, idMedico);
    }

    @Override
    public void procesarCitaCancelada(int idCita) {
        // Punto de extensión: aquí se puede liberar recursos, notificar, etc.
        log.info("[CITA CANCELADA] idCita={}", idCita);
    }
}
