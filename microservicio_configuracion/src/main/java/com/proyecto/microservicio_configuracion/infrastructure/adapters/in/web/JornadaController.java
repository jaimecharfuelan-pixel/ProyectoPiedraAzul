package com.proyecto.microservicio_configuracion.infrastructure.adapters.in.web;

import com.proyecto.microservicio_configuracion.domain.model.JornadaLaboral;
import com.proyecto.microservicio_configuracion.domain.ports.in.GestionarJornadaPort;
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

    private final GestionarJornadaPort gestionarJornada;

    public JornadaController(GestionarJornadaPort gestionarJornada) {
        this.gestionarJornada = gestionarJornada;
    }

    @Operation(summary = "Listar todas las jornadas")
    @GetMapping
    public ResponseEntity<List<JornadaLaboral>> obtenerTodas() {
        return ResponseEntity.ok(gestionarJornada.listarTodas());
    }

    @Operation(summary = "Jornadas por médico", description = "Filtra por medicoId y opcionalmente por día de la semana.")
    @GetMapping(params = "medicoId")
    public ResponseEntity<List<JornadaLaboral>> obtenerPorMedico(
            @RequestParam int medicoId,
            @RequestParam(required = false) String dia) {

        if (dia != null && !dia.isBlank()) {
            Optional<JornadaLaboral> jornada = gestionarJornada.buscarPorMedicoYDia(medicoId, dia);
            return jornada.map(j -> ResponseEntity.ok(List.of(j)))
                    .orElse(ResponseEntity.ok(List.of()));
        }
        return ResponseEntity.ok(gestionarJornada.listarPorMedico(medicoId));
    }

    @Operation(summary = "Jornadas por médico (path)")
    @GetMapping("/medico/{idMedico}")
    public ResponseEntity<List<JornadaLaboral>> obtenerPorMedicoPath(@PathVariable int idMedico) {
        return ResponseEntity.ok(gestionarJornada.listarPorMedico(idMedico));
    }

    @Operation(summary = "Días con jornada de un médico")
    @GetMapping("/medico/{idMedico}/dias")
    public ResponseEntity<List<String>> obtenerDiasConJornada(@PathVariable int idMedico) {
        return ResponseEntity.ok(gestionarJornada.listarDiasConJornada(idMedico));
    }

    @Operation(summary = "Crear jornada")
    @PostMapping
    public ResponseEntity<?> crearJornada(@RequestBody JornadaLaboral jornada) {
        try {
            JornadaLaboral creada = gestionarJornada.crear(jornada);
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
            JornadaLaboral actualizada = gestionarJornada.editar(idJornada, jornada);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar jornada")
    @DeleteMapping("/{idJornada}")
    public ResponseEntity<String> eliminarJornada(@PathVariable int idJornada) {
        if (gestionarJornada.eliminar(idJornada)) {
            return ResponseEntity.ok("Jornada eliminada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jornada no encontrada.");
    }
}
