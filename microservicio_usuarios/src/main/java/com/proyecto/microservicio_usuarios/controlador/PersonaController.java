package com.proyecto.microservicio_usuarios.controlador;

import com.proyecto.microservicio_usuarios.modelo.Persona;
import com.proyecto.microservicio_usuarios.servicio.ServicioPersona;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final ServicioPersona servicioPersona;

    public PersonaController(ServicioPersona servicioPersona) {
        this.servicioPersona = servicioPersona;
    }

    /** GET /api/personas */
    @GetMapping
    public ResponseEntity<List<Persona>> listar() {
        return ResponseEntity.ok(servicioPersona.listarPersonas());
    }

    /** GET /api/personas/documento/{cedula} — RF2: buscar paciente por cédula */
    @GetMapping("/documento/{cedula}")
    public ResponseEntity<?> buscarPorDocumento(@PathVariable String cedula) {
        return servicioPersona.buscarPorDocumento(cedula)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada."));
    }

    /** PUT /api/personas/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<String> editar(@PathVariable int id, @RequestBody Persona persona) {
        persona.setIdPersona(id);
        if (servicioPersona.editarPersona(persona)) {
            return ResponseEntity.ok("Persona actualizada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada.");
    }

    /** DELETE /api/personas/{id} — inactiva la persona */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> inactivar(@PathVariable int id) {
        if (servicioPersona.inactivarPersona(id)) {
            return ResponseEntity.ok("Persona inactivada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada.");
    }
}
