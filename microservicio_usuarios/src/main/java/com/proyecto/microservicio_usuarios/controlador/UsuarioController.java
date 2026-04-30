package com.proyecto.microservicio_usuarios.controlador;

import com.proyecto.microservicio_usuarios.modelo.Usuario;
import com.proyecto.microservicio_usuarios.servicio.ServicioUsuarios;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "CRUD de credenciales de acceso al sistema")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final ServicioUsuarios servicioUsuarios;

    public UsuarioController(ServicioUsuarios servicioUsuarios) {
        this.servicioUsuarios = servicioUsuarios;
    }

    @Operation(summary = "Listar usuarios")
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(servicioUsuarios.listarUsuarios());
    }

    @Operation(summary = "Registrar usuario")
    @PostMapping
    public ResponseEntity<String> registrar(@RequestBody Usuario usuario) {
        if (servicioUsuarios.registrarUsuario(usuario)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado.");
        }
        return ResponseEntity.badRequest().body("Datos inválidos.");
    }

    @Operation(summary = "Editar usuario")
    @PutMapping("/{id}")
    public ResponseEntity<String> editar(@PathVariable int id, @RequestBody Usuario usuario) {
        usuario.setIdUsuario(id);
        if (servicioUsuarios.editarUsuario(usuario)) {
            return ResponseEntity.ok("Usuario actualizado.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
    }

    @Operation(summary = "Eliminar usuario")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        if (servicioUsuarios.eliminarUsuario(id)) {
            return ResponseEntity.ok("Usuario eliminado.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
    }
}
