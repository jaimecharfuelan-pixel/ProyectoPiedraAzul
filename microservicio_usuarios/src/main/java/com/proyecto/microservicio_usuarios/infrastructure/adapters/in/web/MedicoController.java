package com.proyecto.microservicio_usuarios.infrastructure.adapters.in.web;

import com.proyecto.microservicio_usuarios.application.dto.MedicoResumen;
import com.proyecto.microservicio_usuarios.domain.ports.in.GestionarMedicoPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Médicos", description = "Consulta y configuración de médicos activos")
@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final GestionarMedicoPort gestionarMedico;

    public MedicoController(GestionarMedicoPort gestionarMedico) {
        this.gestionarMedico = gestionarMedico;
    }

    @Operation(summary = "Listar médicos activos", description = "Devuelve todos los médicos activos. Usado por ms-configuracion y ms-agendamiento.")
    @GetMapping("/activos")
    public ResponseEntity<List<MedicoResumen>> listarActivos() {
        List<MedicoResumen> medicos = gestionarMedico.listarActivos().stream()
                .map(m -> new MedicoResumen(
                        m.getIdPersona(),
                        m.getNombre(),
                        m.getApellido(),
                        m.getIdEspecialidad(),
                        m.getIdUsuario()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(medicos);
    }

    @Operation(summary = "Asignar especialidad a médico")
    @PutMapping("/{id}/especialidad")
    public ResponseEntity<String> asignarEspecialidad(@PathVariable int id,
                                                       @RequestParam int idEspecialidad) {
        if (gestionarMedico.asignarEspecialidad(id, idEspecialidad)) {
            return ResponseEntity.ok("Especialidad asignada.");
        }
        return ResponseEntity.notFound().build();
    }
}
