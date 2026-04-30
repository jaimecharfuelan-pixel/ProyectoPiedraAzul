package com.proyecto.microservicio_usuarios.controller;

import com.proyecto.microservicio_usuarios.modelo.MedicoTerapista;
import com.proyecto.microservicio_usuarios.modelo.Persona;
import com.proyecto.microservicio_usuarios.servicio.ServicioPersona;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personas")
public class PersonaController {

    private final ServicioPersona servicioPersona;

    public PersonaController(ServicioPersona servicioPersona) {
        this.servicioPersona = servicioPersona;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Persona persona) {
        try {
            return ResponseEntity.ok(servicioPersona.crearPersona(persona));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> editar(@RequestBody Persona persona) {
        boolean ok = servicioPersona.editarPersona(persona);
        return ok ? ResponseEntity.ok("Persona actualizada") :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> inactivar(@PathVariable int id) {
        boolean ok = servicioPersona.inactivarPersona(id);
        return ok ? ResponseEntity.ok("Persona inactivada") :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<Persona> listar() {
        return servicioPersona.listarPersonas();
    }

    @GetMapping("/documento/{cedula}")
    public ResponseEntity<?> buscar(@PathVariable String cedula) {
        return servicioPersona.buscarPorDocumento(cedula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/medicos")
    public List<MedicoTerapista> listarMedicos() {
        return servicioPersona.listarMedicosActivos();
    }

    @PutMapping("/asignar-especialidad")
    public ResponseEntity<?> asignar(@RequestParam int idMedico,
                                     @RequestParam int idEspecialidad) {
        boolean ok = servicioPersona.asignarEspecialidad(idMedico, idEspecialidad);
        return ok ? ResponseEntity.ok("Especialidad asignada") :
                ResponseEntity.notFound().build();
    }
}