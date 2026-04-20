package com.proyecto.microservicio_configuracion.controlador;

import com.proyecto.microservicio_configuracion.modelo.JornadaLaboral;
import com.proyecto.microservicio_configuracion.servicio.ServicioConfiguracion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/jornadas")
public class JornadaController {

    private final ServicioConfiguracion servicio;

    public JornadaController(ServicioConfiguracion servicio) {
        this.servicio = servicio;
    }

    /** GET /api/jornadas — todas las jornadas */
    @GetMapping
    public ResponseEntity<List<JornadaLaboral>> obtenerTodas() {
        return ResponseEntity.ok(servicio.obtenerTodasLasJornadas());
    }

    /**
     * GET /api/jornadas?medicoId=3
     * GET /api/jornadas?medicoId=3&dia=Lunes
     * Usado por ms-agendamiento para consultar disponibilidad.
     */
    @GetMapping(params = "medicoId")
    public ResponseEntity<List<JornadaLaboral>> obtenerPorMedico(
            @RequestParam int medicoId,
            @RequestParam(required = false) String dia) {

        if (dia != null && !dia.isBlank()) {
            Optional<JornadaLaboral> jornada = servicio.obtenerJornadaPorMedicoYDia(medicoId, dia);
            return jornada.map(j -> ResponseEntity.ok(List.of(j)))
                    .orElse(ResponseEntity.ok(List.of()));
        }
        return ResponseEntity.ok(servicio.obtenerJornadasPorMedico(medicoId));
    }

    /** GET /api/jornadas/medico/{idMedico} */
    @GetMapping("/medico/{idMedico}")
    public ResponseEntity<List<JornadaLaboral>> obtenerPorMedicoPath(@PathVariable int idMedico) {
        return ResponseEntity.ok(servicio.obtenerJornadasPorMedico(idMedico));
    }

    /** POST /api/jornadas — RF4: crear jornada */
    @PostMapping
    public ResponseEntity<?> crearJornada(@RequestBody JornadaLaboral jornada) {
        try {
            JornadaLaboral creada = servicio.configurarDisponibilidadMedico(jornada);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** PUT /api/jornadas/{idJornada} — RF4: editar jornada */
    @PutMapping("/{idJornada}")
    public ResponseEntity<?> editarJornada(@PathVariable int idJornada,
                                            @RequestBody JornadaLaboral jornada) {
        try {
            JornadaLaboral actualizada = servicio.editarTurno(idJornada, jornada);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** DELETE /api/jornadas/{idJornada} — RF4: eliminar jornada */
    @DeleteMapping("/{idJornada}")
    public ResponseEntity<String> eliminarJornada(@PathVariable int idJornada) {
        if (servicio.eliminarTurno(idJornada)) {
            return ResponseEntity.ok("Jornada eliminada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jornada no encontrada.");
    }
}
