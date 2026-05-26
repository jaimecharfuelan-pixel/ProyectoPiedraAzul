package com.proyecto.microservicio_usuarios.infrastructure.adapters.in.messaging;

import com.proyecto.microservicio_usuarios.domain.ports.in.ProcesarEventoCitaPort;
import com.proyecto.microservicio_usuarios.infrastructure.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adapter de entrada: recibe eventos de citas desde RabbitMQ
 * y los delega al caso de uso correspondiente.
 */
@Component
public class CitaEventoConsumer {

    private final ProcesarEventoCitaPort procesarEventoCita;

    public CitaEventoConsumer(ProcesarEventoCitaPort procesarEventoCita) {
        this.procesarEventoCita = procesarEventoCita;
    }

    @RabbitListener(queues = RabbitMQConfig.COLA_CITA_CREADA)
    public void onCitaCreada(CitaEventoDTO evento) {
        procesarEventoCita.procesarCitaCreada(evento.getIdCita(), evento.getIdPaciente(), evento.getIdMedico());
    }

    @RabbitListener(queues = RabbitMQConfig.COLA_CITA_CANCELADA)
    public void onCitaCancelada(String idCita) {
        procesarEventoCita.procesarCitaCancelada(Integer.parseInt(idCita));
    }
}
