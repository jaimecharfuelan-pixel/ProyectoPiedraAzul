package com.proyecto.microservicio_agendamiento.domain.ports.out;

import com.proyecto.microservicio_agendamiento.domain.model.Cita;

/**
 * Puerto de salida para publicar eventos de citas.
 * El dominio no sabe si el transporte es RabbitMQ, Kafka u otro.
 */
public interface EventoCitaPublisherPort {
    void publicarCitaCreada(Cita cita);
    void publicarCitaCancelada(int idCita);
}
