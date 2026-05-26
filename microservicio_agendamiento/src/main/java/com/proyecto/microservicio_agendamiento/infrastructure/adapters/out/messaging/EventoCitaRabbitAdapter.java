package com.proyecto.microservicio_agendamiento.infrastructure.adapters.out.messaging;

import com.proyecto.microservicio_agendamiento.domain.model.Cita;
import com.proyecto.microservicio_agendamiento.domain.ports.out.EventoCitaPublisherPort;
import com.proyecto.microservicio_agendamiento.infrastructure.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Adapter de salida: publica eventos de citas en RabbitMQ.
 * Implementa EventoCitaPublisherPort — el dominio no sabe que existe RabbitMQ.
 */
@Component
public class EventoCitaRabbitAdapter implements EventoCitaPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    public EventoCitaRabbitAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicarCitaCreada(Cita cita) {
        String mensaje = cita.getIdCita() + ":" + cita.getIdPaciente() + ":" + cita.getIdMedico()
                + ":" + cita.getFecha() + ":" + cita.getHoraInicio();
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_CITAS,
                RabbitMQConfig.KEY_CITA_CREADA,
                mensaje);
    }

    @Override
    public void publicarCitaCancelada(int idCita) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_CITAS,
                RabbitMQConfig.KEY_CITA_CANCELADA,
                String.valueOf(idCita));
    }
}
