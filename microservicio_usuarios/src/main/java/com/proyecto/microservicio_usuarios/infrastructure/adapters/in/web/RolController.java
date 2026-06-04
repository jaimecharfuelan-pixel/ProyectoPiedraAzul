package com.proyecto.microservicio_usuarios.infrastructure.adapters.in.web;

import com.proyecto.microservicio_usuarios.domain.model.Rol;
import com.proyecto.microservicio_usuarios.domain.ports.in.GestionarRolPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Roles", description = "Asignación y consulta de roles de usuario")
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final GestionarRolPort gestionarRol;

    public RolController(GestionarRolPort gestionarRol) {
        this.gestionarRol = gestionarRol;
    }

    @Operation(summary = "Listar todos los roles")
    @PreAuthorize("hasAuthority('Administrador')")
    @GetMapping
    public ResponseEntity<List<Rol>> listar() {
        return ResponseEntity.ok(gestionarRol.listar());
    }

    @Operation(summary = "Roles de un usuario")
    @PreAuthorize("hasAuthority('Administrador')")
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Rol>> porUsuario(@PathVariable int idUsuario) {
        return ResponseEntity.ok(gestionarRol.listarPorUsuario(idUsuario));
    }

    @Operation(summary = "Asignar rol a usuario")
    @PreAuthorize("hasAuthority('Administrador')")
    @PostMapping
    public ResponseEntity<?> asignar(@RequestBody Rol rol) {
        try {
            Rol asignado = gestionarRol.asignar(rol);
            return ResponseEntity.status(HttpStatus.CREATED).body(asignado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar rol")
    @PreAuthorize("hasAuthority('Administrador')")
    @DeleteMapping("/{idRol}")
    public ResponseEntity<String> eliminar(@PathVariable int idRol) {
        if (gestionarRol.eliminar(idRol)) {
            return ResponseEntity.ok("Rol eliminado.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado.");
    }
}
