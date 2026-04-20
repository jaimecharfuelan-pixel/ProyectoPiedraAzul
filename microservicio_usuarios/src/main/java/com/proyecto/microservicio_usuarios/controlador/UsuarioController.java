package com.proyecto.microservicio_usuarios.controlador;

import com.proyecto.microservicio_usuarios.modelo.Usuario;
import com.proyecto.microservicio_usuarios.servicio.ServicioUsuarios;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final ServicioUsuarios servicioUsuarios;

    public UsuarioController(ServicioUsuarios servicioUsuarios) {
        this.servicioUsuarios = servicioUsuarios;
    }

    /** GET /api/usuarios */
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(servicioUsuarios.listarUsuarios());
    }

    /** POST /api/usuarios */
    @PostMapping
    public ResponseEntity<String> registrar(@RequestBody Usuario usuario) {
        if (servicioUsuarios.registrarUsuario(usuario)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado.");
        }
        return ResponseEntity.badRequest().body("Datos inválidos.");
    }

    /** PUT /api/usuarios/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<String> editar(@PathVariable int id, @RequestBody Usuario usuario) {
        usuario.setIdUsuario(id);
        if (servicioUsuarios.editarUsuario(usuario)) {
            return ResponseEntity.ok("Usuario actualizado.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
    }

    /** DELETE /api/usuarios/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        if (servicioUsuarios.eliminarUsuario(id)) {
            return ResponseEntity.ok("Usuario eliminado.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
    }
}
