package com.proyecto.microservicio_usuarios.infrastructure.adapters.in.web;

import com.proyecto.microservicio_usuarios.application.dto.CrearPersonaCommand;
import com.proyecto.microservicio_usuarios.domain.model.Persona;
import com.proyecto.microservicio_usuarios.domain.ports.in.GestionarPersonaPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Personas", description = "Gestión de datos personales de usuarios del sistema")
@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final GestionarPersonaPort gestionarPersona;

    public PersonaController(GestionarPersonaPort gestionarPersona) {
        this.gestionarPersona = gestionarPersona;
    }

    @Operation(summary = "Listar personas activas")
    @GetMapping
    public ResponseEntity<List<Persona>> listar() {
        return ResponseEntity.ok(gestionarPersona.listar());
    }

    @Operation(summary = "Buscar persona por cédula")
    @GetMapping("/documento/{cedula}")
    public ResponseEntity<?> buscarPorDocumento(@PathVariable String cedula) {
        return gestionarPersona.buscarPorDocumento(cedula)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada."));
    }

    @Operation(summary = "Crear persona (admin)", description = "Crea una persona con usuario y rol asociados.")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearPersonaCommand command) {
        try {
            Persona creada = gestionarPersona.crearAdmin(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Exponer causa raíz para diagnóstico
            String causa = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + causa);
        }
    }

    @Operation(summary = "Editar persona (campos parciales)")
    @PutMapping("/{id}")
    public ResponseEntity<String> editar(@PathVariable int id,
                                          @RequestBody Map<String, Object> campos) {
        if (gestionarPersona.editarCampos(id, campos)) {
            return ResponseEntity.ok("Persona actualizada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada.");
    }

    @Operation(summary = "Inactivar persona")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> inactivar(@PathVariable int id) {
        if (gestionarPersona.inactivar(id)) {
            return ResponseEntity.ok("Persona inactivada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada.");
    }
}
