package com.proyecto.microservicio_agendamiento.controlador;

import com.proyecto.microservicio_agendamiento.modelo.Cita;
import com.proyecto.microservicio_agendamiento.servicio.ServicioAgendamiento;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/citas")
public class AgendamientoController {

    private final ServicioAgendamiento servicio;

    public AgendamientoController(ServicioAgendamiento servicio) {
        this.servicio = servicio;
    }

    /**
     * RF1: Listar citas de un médico en una fecha.
     * GET /api/citas?idMedico=3&fecha=2026-04-21
     * GET /api/citas (todas las de hoy)
     */
    @GetMapping
    public ResponseEntity<List<Cita>> listarCitas(
            @RequestParam(required = false) Integer idMedico,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(servicio.listarCitas(idMedico, fecha));
    }

    /**
     * GET /api/citas/todas
     */
    @GetMapping("/todas")
    public ResponseEntity<List<Cita>> listarTodas() {
        return ResponseEntity.ok(servicio.listarTodasLasCitas());
    }

    /**
     * RF3: Consultar franjas horarias disponibles de un médico en una fecha.
     * GET /api/citas/disponibilidad?idMedico=3&fecha=2026-04-21
     */
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<LocalTime>> consultarDisponibilidad(
            @RequestParam int idMedico,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(servicio.consultarDisponibilidad(idMedico, fecha));
    }

    /**
     * RF3: Paciente agenda cita desde la web.
     * POST /api/citas/web
     * Body: { "idPaciente": 10, "idMedico": 3, "fecha": "2026-04-21", "hora": "07:00" }
     */
    @PostMapping("/web")
    public ResponseEntity<String> agendarCitaWeb(@RequestBody Map<String, String> body) {
        int idPaciente = Integer.parseInt(body.get("idPaciente"));
        int idMedico   = Integer.parseInt(body.get("idMedico"));
        LocalDate fecha = LocalDate.parse(body.get("fecha"));
        LocalTime hora  = LocalTime.parse(body.get("hora"));

        if (servicio.agendarCitaWeb(idPaciente, idMedico, fecha, hora)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Cita agendada.");
        }
        return ResponseEntity.badRequest().body("Horario no disponible.");
    }

    /**
     * RF2: Agendador crea cita manual (paciente por WhatsApp).
     * POST /api/citas
     */
    @PostMapping
    public ResponseEntity<String> crearCitaManual(@RequestBody Cita cita) {
        servicio.crearCitaManual(cita);
        return ResponseEntity.status(HttpStatus.CREATED).body("Cita creada.");
    }

    /**
     * GET /api/citas/paciente/{idPaciente}/historial
     */
    @GetMapping("/paciente/{idPaciente}/historial")
    public ResponseEntity<List<Cita>> historial(@PathVariable int idPaciente) {
        return ResponseEntity.ok(servicio.obtenerHistorialCitas(idPaciente));
    }

    /**
     * GET /api/citas/paciente/{idPaciente}/futuras
     */
    @GetMapping("/paciente/{idPaciente}/futuras")
    public ResponseEntity<List<Cita>> futuras(@PathVariable int idPaciente) {
        return ResponseEntity.ok(servicio.obtenerCitasFuturas(idPaciente));
    }

    /**
     * PUT /api/citas/{idCita}
     */
    @PutMapping("/{idCita}")
    public ResponseEntity<String> editar(@PathVariable int idCita, @RequestBody Cita cita) {
        cita.setIdCita(idCita);
        if (servicio.editarCita(cita)) {
            return ResponseEntity.ok("Cita actualizada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada.");
    }

    /**
     * DELETE /api/citas/{idCita} — cancela la cita
     */
    @DeleteMapping("/{idCita}")
    public ResponseEntity<String> cancelar(@PathVariable int idCita) {
        if (servicio.cancelarCita(idCita)) {
            return ResponseEntity.ok("Cita cancelada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada.");
    }
}
