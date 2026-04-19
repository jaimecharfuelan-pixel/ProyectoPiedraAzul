package com.proyecto.microservicio_configuracion.controlador;

import com.proyecto.microservicio_configuracion.modelo.DominioEspecialidad;
import com.proyecto.microservicio_configuracion.servicio.ServicioConfiguracion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    private final ServicioConfiguracion servicio;

    public EspecialidadController(ServicioConfiguracion servicio) {
        this.servicio = servicio;
    }

    /**
     * Obtener todas las especialidades
     * GET /api/especialidades
     */
    @GetMapping
    public ResponseEntity<List<DominioEspecialidad>> listarEspecialidades() {
        List<DominioEspecialidad> especialidades = servicio.listarEspecialidades();
        return ResponseEntity.ok(especialidades);
    }

    /**
     * Obtener una especialidad específica por ID
     * GET /api/especialidades/{idEspecialidad}
     */
    @GetMapping("/{idEspecialidad}")
    public ResponseEntity<DominioEspecialidad> buscarEspecialidad(@PathVariable int idEspecialidad) {
        Optional<DominioEspecialidad> especialidad = servicio.buscarEspecialidad(idEspecialidad);
        if (especialidad.isPresent()) {
            return ResponseEntity.ok(especialidad.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
