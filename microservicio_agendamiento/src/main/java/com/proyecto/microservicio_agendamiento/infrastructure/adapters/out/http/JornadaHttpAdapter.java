package com.proyecto.microservicio_agendamiento.infrastructure.adapters.out.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.proyecto.microservicio_agendamiento.application.dto.JornadaResumen;
import com.proyecto.microservicio_agendamiento.domain.ports.out.JornadaConsultaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Adapter de salida: consulta jornadas laborales en ms-configuracion via HTTP.
 * Implementa JornadaConsultaPort — el dominio no sabe que existe REST aquí.
 */
@Component
public class JornadaHttpAdapter implements JornadaConsultaPort {

    private final RestTemplate restTemplate;

    @Value("${ms.configuracion.url:http://microservicio-configuracion:8080}")
    private String msConfiguracionUrl;

    public JornadaHttpAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<JornadaResumen> obtenerJornadasPorMedico(int idMedico) {
        try {
            JornadaResumenRaw[] raw = restTemplate.getForObject(
                    msConfiguracionUrl + "/api/jornadas?medicoId=" + idMedico,
                    JornadaResumenRaw[].class);

            if (raw == null) return Collections.emptyList();

            return Arrays.stream(raw)
                    .map(r -> {
                        JornadaResumen j = new JornadaResumen();
                        j.setIdMedico(r.idUsuario);
                        j.setDiaSemana(r.diaSemana);
                        j.setHoraInicio(r.horaInicio);
                        j.setHoraFin(r.horaFin);
                        return j;
                    })
                    .toList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /** Clase interna para deserializar la respuesta JSON de ms-configuracion. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class JornadaResumenRaw {
        public Integer idUsuario;
        public String diaSemana;
        public LocalTime horaInicio;
        public LocalTime horaFin;
    }
}
