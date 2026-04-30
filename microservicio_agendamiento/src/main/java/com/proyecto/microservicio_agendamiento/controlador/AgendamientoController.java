package com.proyecto.microservicio_agendamiento.controlador;

import com.proyecto.microservicio_agendamiento.dto.ReagendarCitaDTO;
import com.proyecto.microservicio_agendamiento.modelo.Cita;
import com.proyecto.microservicio_agendamiento.servicio.ServicioAgendamiento;
import com.proyecto.microservicio_agendamiento.servicio.ServicioAgendamiento.ReagendamientoResultado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Citas", description = "Gestión de citas médicas: agendar, reagendar, cancelar y consultar disponibilidad")
@RestController
@RequestMapping("/api/citas")
public class AgendamientoController {

    private final ServicioAgendamiento servicio;

    public AgendamientoController(ServicioAgendamiento servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Listar citas", description = "Lista citas por médico y/o fecha. Sin parámetros devuelve las citas de hoy.")
    @GetMapping
    public ResponseEntity<List<Cita>> listarCitas(
            @RequestParam(required = false) Integer idMedico,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(servicio.listarCitas(idMedico, fecha));
    }

    @Operation(summary = "Listar todas las citas", description = "Devuelve todas las citas sin filtro.")
    @GetMapping("/todas")
    public ResponseEntity<List<Cita>> listarTodas() {
        return ResponseEntity.ok(servicio.listarTodasLasCitas());
    }

    @Operation(summary = "Consultar disponibilidad", description = "Devuelve los horarios libres de un médico en una fecha específica.")
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<LocalTime>> consultarDisponibilidad(
            @RequestParam int idMedico,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(servicio.consultarDisponibilidad(idMedico, fecha));
    }

    @Operation(summary = "Agendar cita (paciente web)", description = "El paciente agenda su propia cita eligiendo médico, fecha y hora disponible.")
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

    @Operation(summary = "Crear cita manual", description = "El agendador crea una cita manualmente (ej: paciente que llama por teléfono).")
    @PostMapping
    public ResponseEntity<String> crearCitaManual(@RequestBody Cita cita) {
        servicio.crearCitaManual(cita);
        return ResponseEntity.status(HttpStatus.CREATED).body("Cita creada.");
    }

    @Operation(summary = "Historial de citas", description = "Devuelve las citas pasadas de un paciente.")
    @GetMapping("/paciente/{idPaciente}/historial")
    public ResponseEntity<List<Cita>> historial(@PathVariable int idPaciente) {
        return ResponseEntity.ok(servicio.obtenerHistorialCitas(idPaciente));
    }

    @Operation(summary = "Citas futuras", description = "Devuelve las citas próximas de un paciente.")
    @GetMapping("/paciente/{idPaciente}/futuras")
    public ResponseEntity<List<Cita>> futuras(@PathVariable int idPaciente) {
        return ResponseEntity.ok(servicio.obtenerCitasFuturas(idPaciente));
    }

    @Operation(summary = "Editar cita", description = "Actualiza todos los campos de una cita existente.")
    @PutMapping("/{idCita}")
    public ResponseEntity<String> editar(@PathVariable int idCita, @RequestBody Cita cita) {
        cita.setIdCita(idCita);
        if (servicio.editarCita(cita)) {
            return ResponseEntity.ok("Cita actualizada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada.");
    }

    @Operation(summary = "Cancelar cita", description = "Cambia el estado de la cita a Cancelada (id=1). No elimina el registro.")
    @DeleteMapping("/{idCita}")
    public ResponseEntity<String> cancelar(@PathVariable int idCita) {
        if (servicio.cancelarCita(idCita)) {
            return ResponseEntity.ok("Cita cancelada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada.");
    }

    @Operation(summary = "Reagendar cita", description = "Cambia la fecha y hora de una cita. El estado vuelve a Pendiente. No se puede reagendar una cita cancelada.")
    @PatchMapping("/{idCita}/reagendar")
    public ResponseEntity<String> reagendar(
            @PathVariable int idCita,
            @RequestBody ReagendarCitaDTO dto) {

        ReagendamientoResultado resultado = servicio.reagendarCita(idCita, dto.getNuevaFecha(), dto.getNuevaHora());

        return switch (resultado) {
            case OK             -> ResponseEntity.ok("Cita reagendada correctamente.");
            case NO_ENCONTRADA  -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada.");
            case CITA_CANCELADA -> ResponseEntity.badRequest().body("No se puede reagendar una cita cancelada.");
        };
    }
}
