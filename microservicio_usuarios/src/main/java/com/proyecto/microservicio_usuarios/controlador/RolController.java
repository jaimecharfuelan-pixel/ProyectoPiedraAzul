package com.proyecto.microservicio_usuarios.controlador;

import com.proyecto.microservicio_usuarios.modelo.Rol;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioRol;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Roles", description = "Asignación y consulta de roles de usuario")
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RepositorioRol repositorioRol;

    public RolController(RepositorioRol repositorioRol) {
        this.repositorioRol = repositorioRol;
    }

    @Operation(summary = "Listar todos los roles")
    @GetMapping
    public ResponseEntity<List<Rol>> listar() {
        return ResponseEntity.ok(repositorioRol.findAll());
    }

    @Operation(summary = "Roles de un usuario")
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Rol>> porUsuario(@PathVariable int idUsuario) {
        return ResponseEntity.ok(repositorioRol.findByIdUsuario(idUsuario));
    }

    @Operation(summary = "Asignar rol a usuario")
    @PostMapping
    public ResponseEntity<String> asignar(@RequestBody Rol rol) {
        if (rol.getNombre() == null || rol.getNombre().isBlank() || rol.getIdUsuario() == 0) {
            return ResponseEntity.badRequest().body("Nombre de rol e idUsuario son obligatorios.");
        }
        repositorioRol.save(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body("Rol asignado correctamente.");
    }

    @Operation(summary = "Eliminar rol")
    @DeleteMapping("/{idRol}")
    public ResponseEntity<String> eliminar(@PathVariable int idRol) {
        if (!repositorioRol.existsById(idRol)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado.");
        }
        repositorioRol.deleteById(idRol);
        return ResponseEntity.ok("Rol eliminado.");
    }
}
