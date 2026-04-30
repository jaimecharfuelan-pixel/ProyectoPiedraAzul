package com.proyecto.microservicio_agendamiento.mensajeria;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_CITAS      = "exchange.citas";
    public static final String COLA_CITA_CREADA    = "cola.cita.creada";
    public static final String COLA_CITA_CANCELADA = "cola.cita.cancelada";
    public static final String KEY_CITA_CREADA     = "cita.creada";
    public static final String KEY_CITA_CANCELADA  = "cita.cancelada";

    @Bean
    public TopicExchange exchangeCitas() {
        return new TopicExchange(EXCHANGE_CITAS);
    }

    @Bean
    public Queue colaCitaCreada() {
        return new Queue(COLA_CITA_CREADA, true);
    }

    @Bean
    public Queue colaCitaCancelada() {
        return new Queue(COLA_CITA_CANCELADA, true);
    }

    @Bean
    public Binding bindingCitaCreada(Queue colaCitaCreada, TopicExchange exchangeCitas) {
        return BindingBuilder.bind(colaCitaCreada).to(exchangeCitas).with(KEY_CITA_CREADA);
    }

    @Bean
    public Binding bindingCitaCancelada(Queue colaCitaCancelada, TopicExchange exchangeCitas) {
        return BindingBuilder.bind(colaCitaCancelada).to(exchangeCitas).with(KEY_CITA_CANCELADA);
    }
}
