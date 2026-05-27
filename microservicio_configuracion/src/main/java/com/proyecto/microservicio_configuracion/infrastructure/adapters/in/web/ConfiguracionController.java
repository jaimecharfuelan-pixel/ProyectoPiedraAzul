package com.proyecto.microservicio_configuracion.infrastructure.adapters.in.web;

import com.proyecto.microservicio_configuracion.domain.model.MedicoResumen;
import com.proyecto.microservicio_configuracion.domain.ports.in.ConsultarMedicosPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Configuración", description = "Endpoints de apoyo para la pantalla de configuración")
@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

    private final ConsultarMedicosPort consultarMedicos;

    public ConfiguracionController(ConsultarMedicosPort consultarMedicos) {
        this.consultarMedicos = consultarMedicos;
    }

    @Operation(summary = "Listar médicos activos", description = "Consulta ms-usuarios para obtener médicos activos. Usado al asignar jornadas.")
    @GetMapping("/medicos")
    public ResponseEntity<List<MedicoResumen>> listarMedicos() {
        return ResponseEntity.ok(consultarMedicos.listarActivos());
    }
}
