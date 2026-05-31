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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Consulta jornadas laborales en ms-configuracion.
 * idMedico en citas = id_persona; jornada_laboral usa id_usuario.
 */
@Component
public class JornadaHttpAdapter implements JornadaConsultaPort {

    private final RestTemplate restTemplate;
    private final Map<Integer, Integer> cacheIdUsuario = new ConcurrentHashMap<>();

    @Value("${ms.configuracion.url:http://microservicio-configuracion:8080}")
    private String msConfiguracionUrl;

    @Value("${ms.usuarios.url:http://microservicio-usuarios:8080}")
    private String msUsuariosUrl;

    public JornadaHttpAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<JornadaResumen> obtenerJornadasPorMedico(int idMedico) {
        int idUsuario = resolverIdUsuario(idMedico);
        try {
            JornadaResumenRaw[] raw = restTemplate.getForObject(
                    msConfiguracionUrl + "/api/jornadas?medicoId=" + idUsuario,
                    JornadaResumenRaw[].class);

            if (raw == null) return Collections.emptyList();

            return Arrays.stream(raw)
                    .map(r -> {
                        JornadaResumen j = new JornadaResumen();
                        j.setIdMedico(idMedico);
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

    private int resolverIdUsuario(int idPersona) {
        return cacheIdUsuario.computeIfAbsent(idPersona, id -> {
            try {
                MedicoRaw[] medicos = restTemplate.getForObject(
                        msUsuariosUrl + "/api/medicos/activos", MedicoRaw[].class);
                if (medicos == null) return idPersona;
                return Arrays.stream(medicos)
                        .filter(m -> m.idMedico == idPersona && m.idUsuario != null)
                        .map(m -> m.idUsuario)
                        .findFirst()
                        .orElse(idPersona);
            } catch (Exception e) {
                return idPersona;
            }
        });
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class JornadaResumenRaw {
        public Integer idUsuario;
        public String diaSemana;
        public LocalTime horaInicio;
        public LocalTime horaFin;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class MedicoRaw {
        public int idMedico;
        public Integer idUsuario;
    }
}
