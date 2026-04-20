package com.proyecto.microservicio_usuarios.controlador;

import com.proyecto.microservicio_usuarios.dto.RegistroPacienteDTO;
import com.proyecto.microservicio_usuarios.modelo.Paciente;
import com.proyecto.microservicio_usuarios.servicio.ServicioPaciente;
import com.proyecto.microservicio_usuarios.servicio.ServicioPersona;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final ServicioPaciente servicioPaciente;
    private final ServicioPersona servicioPersona;

    public PacienteController(ServicioPaciente servicioPaciente, ServicioPersona servicioPersona) {
        this.servicioPaciente = servicioPaciente;
        this.servicioPersona = servicioPersona;
    }

    /** GET /api/pacientes */
    @GetMapping
    public ResponseEntity<List<Paciente>> listar() {
        return ResponseEntity.ok(servicioPaciente.listarPacientes());
    }

    /** GET /api/pacientes/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        return servicioPaciente.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente no encontrado."));
    }

    /**
     * POST /api/pacientes
     * RF2: Agendador registra paciente que contactó por WhatsApp.
     * RF3: Paciente se registra para agendar cita web.
     * Recibe paciente + usuario en un solo DTO.
     */
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody RegistroPacienteDTO dto) {
        try {
            Paciente creado = servicioPaciente.registrarPaciente(dto.getPaciente(), dto.getUsuario());
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /api/pacientes/documento/{cedula}
     * RF2: Buscar paciente existente por cédula antes de crear la cita.
     */
    @GetMapping("/documento/{cedula}")
    public ResponseEntity<?> buscarPorDocumento(@PathVariable String cedula) {
        return servicioPersona.buscarPorDocumento(cedula)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente no encontrado."));
    }
}
