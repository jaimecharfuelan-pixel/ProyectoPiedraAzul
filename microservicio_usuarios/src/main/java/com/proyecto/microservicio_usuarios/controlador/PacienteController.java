package com.proyecto.microservicio_usuarios.controlador;

import com.proyecto.microservicio_usuarios.dto.RegistroPacienteDTO;
import com.proyecto.microservicio_usuarios.modelo.Paciente;
import com.proyecto.microservicio_usuarios.servicio.ServicioPaciente;
import com.proyecto.microservicio_usuarios.servicio.ServicioPersona;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pacientes", description = "Registro y consulta de pacientes")
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final ServicioPaciente servicioPaciente;
    private final ServicioPersona servicioPersona;

    public PacienteController(ServicioPaciente servicioPaciente, ServicioPersona servicioPersona) {
        this.servicioPaciente = servicioPaciente;
        this.servicioPersona = servicioPersona;
    }

    @Operation(summary = "Listar pacientes")
    @GetMapping
    public ResponseEntity<List<Paciente>> listar() {
        return ResponseEntity.ok(servicioPaciente.listarPacientes());
    }

    @Operation(summary = "Buscar paciente por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        return servicioPaciente.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente no encontrado."));
    }

    @Operation(summary = "Registrar paciente", description = "Crea paciente + usuario en una sola llamada.")
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody RegistroPacienteDTO dto) {
        try {
            Paciente creado = servicioPaciente.registrarPaciente(dto.getPaciente(), dto.getUsuario());
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Buscar paciente por cédula")
    @GetMapping("/documento/{cedula}")
    public ResponseEntity<?> buscarPorDocumento(@PathVariable String cedula) {
        return servicioPersona.buscarPorDocumento(cedula)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente no encontrado."));
    }
}
