package com.proyecto.microservicio_usuarios.mensajeria;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COLA_CITA_CREADA    = "cola.cita.creada";
    public static final String COLA_CITA_CANCELADA = "cola.cita.cancelada";

    @Bean
    public Queue colaCitaCreada() {
        return new Queue(COLA_CITA_CREADA, true);
    }

    @Bean
    public Queue colaCitaCancelada() {
        return new Queue(COLA_CITA_CANCELADA, true);
    }
}
