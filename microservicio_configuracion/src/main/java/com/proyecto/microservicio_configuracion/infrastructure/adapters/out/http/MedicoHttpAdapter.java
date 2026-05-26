package com.proyecto.microservicio_configuracion.infrastructure.adapters.out.http;

import com.proyecto.microservicio_configuracion.domain.model.MedicoResumen;
import com.proyecto.microservicio_configuracion.domain.ports.out.MedicoClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Adapter de salida: consulta médicos activos en ms-usuarios via HTTP.
 * Implementa MedicoClientPort — el dominio no sabe que existe REST aquí.
 */
@Component
public class MedicoHttpAdapter implements MedicoClientPort {

    private final RestTemplate restTemplate;

    @Value("${ms.usuarios.url:http://microservicio-usuarios:8080}")
    private String msUsuariosUrl;

    public MedicoHttpAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<MedicoResumen> listarMedicosActivos() {
        try {
            MedicoResumen[] medicos = restTemplate.getForObject(
                    msUsuariosUrl + "/api/medicos/activos", MedicoResumen[].class);
            return medicos != null ? Arrays.asList(medicos) : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
