package com.proyecto.microservicio_configuracion.infrastructure.adapters.in.web;

import com.proyecto.microservicio_configuracion.domain.model.Especialidad;
import com.proyecto.microservicio_configuracion.domain.ports.in.ConsultarEspecialidadPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Especialidades", description = "Consulta de especialidades médicas disponibles")
@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    private final ConsultarEspecialidadPort consultarEspecialidad;

    public EspecialidadController(ConsultarEspecialidadPort consultarEspecialidad) {
        this.consultarEspecialidad = consultarEspecialidad;
    }

    @Operation(summary = "Listar especialidades")
    @GetMapping
    public ResponseEntity<List<Especialidad>> listar() {
        return ResponseEntity.ok(consultarEspecialidad.listar());
    }

    @Operation(summary = "Buscar especialidad por ID")
    @GetMapping("/{idEspecialidad}")
    public ResponseEntity<Especialidad> buscar(@PathVariable int idEspecialidad) {
        return consultarEspecialidad.buscarPorId(idEspecialidad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
