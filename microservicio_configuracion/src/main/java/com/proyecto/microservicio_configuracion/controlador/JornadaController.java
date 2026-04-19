package com.proyecto.microservicio_configuracion.controlador;

import com.proyecto.microservicio_configuracion.modelo.JornadaLaboral;
import com.proyecto.microservicio_configuracion.servicio.ServicioConfiguracion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jornadas")
public class JornadaController {

    private final ServicioConfiguracion servicio;

    public JornadaController(ServicioConfiguracion servicio) {
        this.servicio = servicio;
    }

    /**
     * Obtener todas las jornadas laborales
     * GET /api/jornadas
     */
    @GetMapping
    public ResponseEntity<List<JornadaLaboral>> obtenerTodasLasJornadas() {
        List<JornadaLaboral> jornadas = servicio.obtenerTodasLasJornadas();
        return ResponseEntity.ok(jornadas);
    }

    /**
     * Obtener jornadas de un médico específico
     * GET /api/jornadas/medico/{idMedico}
     */
    @GetMapping("/medico/{idMedico}")
    public ResponseEntity<List<JornadaLaboral>> obtenerJornadasPorMedico(@PathVariable int idMedico) {
        List<JornadaLaboral> jornadas = servicio.obtenerJornadasPorMedico(idMedico);
        return ResponseEntity.ok(jornadas);
    }

    /**
     * Crear una nueva jornada laboral
     * POST /api/jornadas
     */
    @PostMapping
    public ResponseEntity<String> crearJornada(@RequestBody JornadaLaboral jornada) {
        try {
            boolean creada = servicio.configurarDisponibilidadMedico(jornada);
            if (creada) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Jornada creada exitosamente");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo crear la jornada");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * Editar una jornada laboral existente
     * PUT /api/jornadas/{idJornada}
     */
    @PutMapping("/{idJornada}")
    public ResponseEntity<String> editarJornada(
            @PathVariable int idJornada,
            @RequestBody JornadaLaboral jornada) {
        jornada.setIdJornada(idJornada);
        boolean editada = servicio.editarTurno(jornada);
        if (editada) {
            return ResponseEntity.ok("Jornada actualizada exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jornada no encontrada");
        }
    }

    /**
     * Eliminar una jornada laboral
     * DELETE /api/jornadas/{idJornada}
     */
    @DeleteMapping("/{idJornada}")
    public ResponseEntity<String> eliminarJornada(@PathVariable int idJornada) {
        boolean eliminada = servicio.eliminarTurno(idJornada);
        if (eliminada) {
            return ResponseEntity.ok("Jornada eliminada exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jornada no encontrada");
        }
    }
}
