package com.proyecto.microservicio_usuarios.infrastructure.adapters.in.web;

import com.proyecto.microservicio_usuarios.domain.model.Usuario;
import com.proyecto.microservicio_usuarios.domain.ports.in.GestionarUsuarioPort;
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

    private final GestionarUsuarioPort gestionarUsuario;

    public UsuarioController(GestionarUsuarioPort gestionarUsuario) {
        this.gestionarUsuario = gestionarUsuario;
    }

    @Operation(summary = "Listar usuarios")
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(gestionarUsuario.listar());
    }

    @Operation(summary = "Registrar usuario")
    @PostMapping
    public ResponseEntity<String> registrar(@RequestBody Usuario usuario) {
        if (gestionarUsuario.registrar(usuario)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado.");
        }
        return ResponseEntity.badRequest().body("Datos inválidos.");
    }

    @Operation(summary = "Editar usuario")
    @PutMapping("/{id}")
    public ResponseEntity<String> editar(@PathVariable int id, @RequestBody Usuario usuario) {
        usuario.setIdUsuario(id);
        if (gestionarUsuario.editar(usuario)) {
            return ResponseEntity.ok("Usuario actualizado.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
    }

    @Operation(summary = "Eliminar usuario")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        if (gestionarUsuario.eliminar(id)) {
            return ResponseEntity.ok("Usuario eliminado.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
    }
}
