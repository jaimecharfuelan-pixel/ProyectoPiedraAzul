package com.proyecto.microservicio_usuarios.controller;

import com.proyecto.microservicio_usuarios.modelo.Usuario;
import com.proyecto.microservicio_usuarios.servicio.ServicioUsuarios;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final ServicioUsuarios servicioUsuarios;

    public UsuarioController(ServicioUsuarios servicioUsuarios) {
        this.servicioUsuarios = servicioUsuarios;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Usuario usuario) {
        boolean ok = servicioUsuarios.registrarUsuario(usuario);
        return ok ? ResponseEntity.ok("Usuario creado") :
                ResponseEntity.badRequest().body("Datos inválidos");
    }

    @PutMapping
    public ResponseEntity<?> editar(@RequestBody Usuario usuario) {
        boolean ok = servicioUsuarios.editarUsuario(usuario);
        return ok ? ResponseEntity.ok("Usuario actualizado") :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {
        boolean ok = servicioUsuarios.eliminarUsuario(id);
        return ok ? ResponseEntity.ok("Usuario eliminado") :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<Usuario> listar() {
        return servicioUsuarios.listarUsuarios();
    }
}