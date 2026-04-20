package com.proyecto.microservicio_configuracion.controlador;

import com.proyecto.microservicio_configuracion.dto.MedicoResumenDTO;
import com.proyecto.microservicio_configuracion.servicio.ServicioMedicoCliente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints de apoyo para la pantalla de configuración (RF4).
 */
@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

    private final ServicioMedicoCliente servicioMedicoCliente;

    public ConfiguracionController(ServicioMedicoCliente servicioMedicoCliente) {
        this.servicioMedicoCliente = servicioMedicoCliente;
    }

    /**
     * GET /api/configuracion/medicos
     * Devuelve la lista de médicos activos consultando ms-usuarios.
     * Usado por el admin para seleccionar a qué médico asignar una jornada.
     */
    @GetMapping("/medicos")
    public ResponseEntity<List<MedicoResumenDTO>> listarMedicos() {
        return ResponseEntity.ok(servicioMedicoCliente.listarMedicosActivos());
    }
}
