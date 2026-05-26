package com.proyecto.microservicio_agendamiento.infrastructure.adapters.in.web;

import com.proyecto.microservicio_agendamiento.application.dto.AgendarCitaWebCommand;
import com.proyecto.microservicio_agendamiento.application.dto.CrearCitaManualCommand;
import com.proyecto.microservicio_agendamiento.application.dto.ReagendarCitaCommand;
import com.proyecto.microservicio_agendamiento.application.usecase.GestionarCitaUseCase.ReagendamientoResultado;
import com.proyecto.microservicio_agendamiento.domain.model.Cita;
import com.proyecto.microservicio_agendamiento.domain.ports.in.AgendarCitaPort;
import com.proyecto.microservicio_agendamiento.domain.ports.in.ConsultarDisponibilidadPort;
import com.proyecto.microservicio_agendamiento.domain.ports.in.GestionarCitaPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Tag(name = "Citas", description = "Gestión de citas médicas: agendar, reagendar, cancelar y consultar disponibilidad")
@RestController
@RequestMapping("/api/citas")
public class AgendamientoController {

    private final AgendarCitaPort agendarCita;
    private final GestionarCitaPort gestionarCita;
    private final ConsultarDisponibilidadPort consultarDisponibilidad;

    public AgendamientoController(AgendarCitaPort agendarCita,
                                   GestionarCitaPort gestionarCita,
                                   ConsultarDisponibilidadPort consultarDisponibilidad) {
        this.agendarCita            = agendarCita;
        this.gestionarCita          = gestionarCita;
        this.consultarDisponibilidad = consultarDisponibilidad;
    }

    @Operation(summary = "Listar citas", description = "Lista citas por médico y/o fecha. Sin parámetros devuelve las citas de hoy.")
    @GetMapping
    public ResponseEntity<List<Cita>> listarCitas(
            @RequestParam(required = false) Integer idMedico,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(gestionarCita.listar(idMedico, fecha));
    }

    @Operation(summary = "Listar todas las citas")
    @GetMapping("/todas")
    public ResponseEntity<List<Cita>> listarTodas() {
        return ResponseEntity.ok(gestionarCita.listarTodas());
    }

    @Operation(summary = "Consultar disponibilidad", description = "Devuelve los horarios libres de un médico en una fecha.")
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<LocalTime>> consultarDisponibilidad(
            @RequestParam int idMedico,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(consultarDisponibilidad.consultar(idMedico, fecha));
    }

    @Operation(summary = "Agendar cita (paciente web)", description = "El paciente agenda su cita eligiendo médico, fecha y hora disponible.")
    @PostMapping("/web")
    public ResponseEntity<String> agendarCitaWeb(@RequestBody AgendarCitaWebCommand command) {
        if (agendarCita.agendarWeb(command)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Cita agendada.");
        }
        return ResponseEntity.badRequest().body("Horario no disponible.");
    }

    @Operation(summary = "Crear cita manual", description = "El agendador crea una cita manualmente.")
    @PostMapping
    public ResponseEntity<String> crearCitaManual(@RequestBody CrearCitaManualCommand command) {
        try {
            agendarCita.crearManual(command);
            return ResponseEntity.status(HttpStatus.CREATED).body("Cita creada.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Historial de citas del paciente")
    @GetMapping("/paciente/{idPaciente}/historial")
    public ResponseEntity<List<Cita>> historial(@PathVariable int idPaciente) {
        return ResponseEntity.ok(gestionarCita.historialPaciente(idPaciente));
    }

    @Operation(summary = "Citas futuras del paciente")
    @GetMapping("/paciente/{idPaciente}/futuras")
    public ResponseEntity<List<Cita>> futuras(@PathVariable int idPaciente) {
        return ResponseEntity.ok(gestionarCita.citasFuturasPaciente(idPaciente));
    }

    @Operation(summary = "Editar cita")
    @PutMapping("/{idCita}")
    public ResponseEntity<String> editar(@PathVariable int idCita, @RequestBody Cita cita) {
        cita.setIdCita(idCita);
        if (gestionarCita.editar(cita)) {
            return ResponseEntity.ok("Cita actualizada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada.");
    }

    @Operation(summary = "Cancelar cita", description = "Cambia el estado a Cancelada. No elimina el registro.")
    @DeleteMapping("/{idCita}")
    public ResponseEntity<String> cancelar(@PathVariable int idCita) {
        if (gestionarCita.cancelar(idCita)) {
            return ResponseEntity.ok("Cita cancelada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada.");
    }

    @Operation(summary = "Reagendar cita", description = "Cambia fecha/hora. El estado vuelve a Pendiente. No aplica a citas canceladas.")
    @PatchMapping("/{idCita}/reagendar")
    public ResponseEntity<String> reagendar(@PathVariable int idCita,
                                             @RequestBody ReagendarCitaCommand command) {
        command.setIdCita(idCita);
        ReagendamientoResultado resultado = gestionarCita.reagendar(command);
        return switch (resultado) {
            case OK             -> ResponseEntity.ok("Cita reagendada correctamente.");
            case NO_ENCONTRADA  -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada.");
            case CITA_CANCELADA -> ResponseEntity.badRequest().body("No se puede reagendar una cita cancelada.");
        };
    }
}
