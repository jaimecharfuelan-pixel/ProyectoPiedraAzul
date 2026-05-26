package com.proyecto.microservicio_usuarios.infrastructure.adapters.in.web;

import com.proyecto.microservicio_usuarios.application.dto.LoginCommand;
import com.proyecto.microservicio_usuarios.application.dto.LoginResult;
import com.proyecto.microservicio_usuarios.domain.ports.in.AutenticarUsuarioPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "Login, logout y validación de tokens")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AutenticarUsuarioPort autenticarUsuario;

    public AuthController(AutenticarUsuarioPort autenticarUsuario) {
        this.autenticarUsuario = autenticarUsuario;
    }

    @Operation(summary = "Login", description = "Autentica un usuario y devuelve un token de sesión.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginCommand command) {
        LoginResult result = autenticarUsuario.login(command);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Logout", description = "Cierra la sesión invalidando el token.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        autenticarUsuario.cerrarSesion(token.replace("Bearer ", ""));
        return ResponseEntity.ok("Sesión cerrada.");
    }

    @Operation(summary = "Validar token", description = "Verifica si un token de sesión es válido.")
    @GetMapping("/validar")
    public ResponseEntity<Boolean> validar(@RequestParam String token) {
        return ResponseEntity.ok(autenticarUsuario.validarToken(token));
    }
}
