package com.proyecto.microservicio_configuracion.servicio;

import com.proyecto.microservicio_configuracion.dto.MedicoResumenDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Cliente HTTP que consulta ms-usuarios para obtener la lista de médicos activos.
 * Usado en RF4 para que el admin pueda seleccionar a qué médico asignar una jornada.
 */
@Service
public class ServicioMedicoCliente {

    private final RestTemplate restTemplate;

    @Value("${ms.usuarios.url:http://ms-usuarios:8081}")
    private String msUsuariosUrl;

    public ServicioMedicoCliente(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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
