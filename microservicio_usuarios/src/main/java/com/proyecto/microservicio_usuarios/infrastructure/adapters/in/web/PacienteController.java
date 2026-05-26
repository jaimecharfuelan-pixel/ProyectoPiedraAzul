package com.proyecto.microservicio_usuarios.infrastructure.adapters.in.web;

import com.proyecto.microservicio_usuarios.application.dto.RegistrarPacienteCommand;
import com.proyecto.microservicio_usuarios.domain.model.Paciente;
import com.proyecto.microservicio_usuarios.domain.model.Persona;
import com.proyecto.microservicio_usuarios.domain.ports.in.GestionarPersonaPort;
import com.proyecto.microservicio_usuarios.domain.ports.in.RegistrarPacientePort;
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

    private final RegistrarPacientePort registrarPaciente;
    private final GestionarPersonaPort gestionarPersona;

    public PacienteController(RegistrarPacientePort registrarPaciente,
                               GestionarPersonaPort gestionarPersona) {
        this.registrarPaciente = registrarPaciente;
        this.gestionarPersona = gestionarPersona;
    }

    @Operation(summary = "Listar pacientes")
    @GetMapping
    public ResponseEntity<List<Paciente>> listar() {
        return ResponseEntity.ok(registrarPaciente.listar());
    }

    @Operation(summary = "Buscar paciente por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        return registrarPaciente.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente no encontrado."));
    }

    @Operation(summary = "Registrar paciente", description = "Crea paciente + usuario en una sola llamada.")
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody RegistrarPacienteCommand command) {
        try {
            Paciente creado = registrarPaciente.registrar(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Buscar paciente por cédula")
    @GetMapping("/documento/{cedula}")
    public ResponseEntity<?> buscarPorDocumento(@PathVariable String cedula) {
        return gestionarPersona.buscarPorDocumento(cedula)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente no encontrado."));
    }
}
