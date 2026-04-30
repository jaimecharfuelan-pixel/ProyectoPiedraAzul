package com.proyecto.microservicio_usuarios.mensajeria;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumidorCitas {

    /**
     * Recibe mensaje: "idCita:idPaciente:idMedico:fecha:hora"
     */
    @RabbitListener(queues = RabbitMQConfig.COLA_CITA_CREADA)
    public void procesarCitaCreada(String mensaje) {
        System.out.println("[CITA CREADA] " + mensaje);
    }

    @RabbitListener(queues = RabbitMQConfig.COLA_CITA_CANCELADA)
    public void procesarCitaCancelada(String idCita) {
        System.out.println("[CITA CANCELADA] ID: " + idCita);
    }
}
