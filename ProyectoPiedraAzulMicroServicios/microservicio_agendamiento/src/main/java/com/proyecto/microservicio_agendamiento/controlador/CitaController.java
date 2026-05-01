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

@RestController
@RequestMapping("/api/agendamiento/citas")
public class CitaController {

    private final ServicioAgendamiento servicioAgendamiento;

    public CitaController(ServicioAgendamiento servicioAgendamiento) {
        this.servicioAgendamiento = servicioAgendamiento;
    }

    @GetMapping
    public ResponseEntity<List<Cita>> listarCitas(
            @RequestParam(required = false) Integer idMedico,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(servicioAgendamiento.listarCitas(idMedico, fecha));
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<List<LocalTime>> consultarDisponibilidad(
            @RequestParam Integer idMedico,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(servicioAgendamiento.consultarDisponibilidad(idMedico, fecha));
    }

    @GetMapping("/paciente/{idPaciente}/historial")
    public ResponseEntity<List<Cita>> historialPaciente(@PathVariable int idPaciente) {
        return ResponseEntity.ok(servicioAgendamiento.obtenerHistorialCitas(idPaciente));
    }

    @GetMapping("/paciente/{idPaciente}/futuras")
    public ResponseEntity<List<Cita>> citasFuturasPaciente(@PathVariable int idPaciente) {
        return ResponseEntity.ok(servicioAgendamiento.obtenerCitasFuturas(idPaciente));
    }

    @PostMapping
    public ResponseEntity<Cita> crearCita(@RequestBody Cita cita) {
        Cita creada = servicioAgendamiento.crearCitaManual(cita) ? cita : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PostMapping("/agendar")
    public ResponseEntity<String> agendarCita(@RequestBody Cita cita) {
        boolean agendada = servicioAgendamiento.agendarCitaWeb(
                cita.getIdPaciente(),
                cita.getIdMedico(),
                cita.getFecha(),
                cita.getHoraInicio()
        );
        if (agendada) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Cita agendada correctamente");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("No hay disponibilidad para la cita solicitada");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> editarCita(@PathVariable int id, @RequestBody Cita cita) {
        cita.setIdCita(id);
        boolean actualizado = servicioAgendamiento.editarCita(cita);
        return actualizado
                ? ResponseEntity.ok("Cita actualizada correctamente")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelarCita(@PathVariable int id) {
        boolean cancelado = servicioAgendamiento.cancelarCita(id);
        return cancelado
                ? ResponseEntity.ok("Cita cancelada correctamente")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada");
    }
}
