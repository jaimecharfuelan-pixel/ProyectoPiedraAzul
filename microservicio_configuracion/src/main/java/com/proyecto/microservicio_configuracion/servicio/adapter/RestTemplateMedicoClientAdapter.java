package com.proyecto.microservicio_configuracion.servicio.adapter;

import com.proyecto.microservicio_configuracion.dto.MedicoResumenDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class RestTemplateMedicoClientAdapter implements MedicoClientPort {

    private final RestTemplate restTemplate;

    @Value("${ms.usuarios.url:http://ms-usuarios:8081}")
    private String msUsuariosUrl;

    public RestTemplateMedicoClientAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<MedicoResumenDTO> listarMedicosActivos() {
        try {
            MedicoResumenDTO[] medicos = restTemplate.getForObject(
                    msUsuariosUrl + "/api/medicos/activos", MedicoResumenDTO[].class);
            return medicos != null ? Arrays.asList(medicos) : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
