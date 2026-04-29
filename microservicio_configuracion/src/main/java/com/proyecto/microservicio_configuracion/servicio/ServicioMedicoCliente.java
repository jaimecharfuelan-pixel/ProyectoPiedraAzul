package com.proyecto.microservicio_configuracion.servicio;

import com.proyecto.microservicio_configuracion.dto.MedicoResumenDTO;
import com.proyecto.microservicio_configuracion.servicio.adapter.MedicoClientPort;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Cliente HTTP que consulta ms-usuarios para obtener la lista de médicos activos.
 * Usado en RF4 para que el admin pueda seleccionar a qué médico asignar una jornada.
 */
@Service
public class ServicioMedicoCliente {

    private final MedicoClientPort medicoClientPort;

    public ServicioMedicoCliente(MedicoClientPort medicoClientPort) {
        this.medicoClientPort = medicoClientPort;
    }

    public List<MedicoResumenDTO> listarMedicosActivos() {
        return medicoClientPort.listarMedicosActivos();
    }
}
