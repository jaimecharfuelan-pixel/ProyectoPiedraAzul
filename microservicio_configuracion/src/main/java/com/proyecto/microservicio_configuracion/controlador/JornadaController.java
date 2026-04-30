package com.proyecto.microservicio_configuracion.controlador;

import com.proyecto.microservicio_configuracion.modelo.JornadaLaboral;
import com.proyecto.microservicio_configuracion.servicio.ServicioConfiguracion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Jornadas", description = "Gestión de jornadas laborales de médicos")
@RestController
@RequestMapping("/api/jornadas")
public class JornadaController {

    private final ServicioConfiguracion servicio;

    public JornadaController(ServicioConfiguracion servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Listar todas las jornadas")
    @GetMapping
    public ResponseEntity<List<JornadaLaboral>> obtenerTodas() {
        return ResponseEntity.ok(servicio.obtenerTodasLasJornadas());
    }

    @Operation(summary = "Jornadas por médico", description = "Filtra por medicoId y opcionalmente por día de la semana.")
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

    @Operation(summary = "Jornadas por médico (path)")
    @GetMapping("/medico/{idMedico}")
    public ResponseEntity<List<JornadaLaboral>> obtenerPorMedicoPath(@PathVariable int idMedico) {
        return ResponseEntity.ok(servicio.obtenerJornadasPorMedico(idMedico));
    }

    @Operation(summary = "Crear jornada", description = "Asigna una nueva jornada laboral a un médico.")
    @PostMapping
    public ResponseEntity<?> crearJornada(@RequestBody JornadaLaboral jornada) {
        try {
            JornadaLaboral creada = servicio.configurarDisponibilidadMedico(jornada);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Editar jornada")
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

    @Operation(summary = "Eliminar jornada")
    @DeleteMapping("/{idJornada}")
    public ResponseEntity<String> eliminarJornada(@PathVariable int idJornada) {
        if (servicio.eliminarTurno(idJornada)) {
            return ResponseEntity.ok("Jornada eliminada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jornada no encontrada.");
    }
}
