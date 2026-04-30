package com.proyecto.microservicio_agendamiento.mensajeria;

import com.proyecto.microservicio_agendamiento.modelo.Cita;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PublicadorCitas {

    private final RabbitTemplate rabbitTemplate;

    public PublicadorCitas(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarCitaCreada(Cita cita) {
        // Manda como String simple para evitar problemas de serialización
        String mensaje = cita.getIdCita() + ":" + cita.getIdPaciente() + ":" + cita.getIdMedico()
                + ":" + cita.getFecha() + ":" + cita.getHoraInicio();
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_CITAS,
                RabbitMQConfig.KEY_CITA_CREADA,
                mensaje);
    }

    public void publicarCitaCancelada(int idCita) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_CITAS,
                RabbitMQConfig.KEY_CITA_CANCELADA,
                String.valueOf(idCita));
    }
}
