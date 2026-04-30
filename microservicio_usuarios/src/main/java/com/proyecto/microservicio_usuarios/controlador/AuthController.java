package com.proyecto.microservicio_usuarios.controlador;

import com.proyecto.microservicio_usuarios.dto.LoginRequestDTO;
import com.proyecto.microservicio_usuarios.dto.LoginResponseDTO;
import com.proyecto.microservicio_usuarios.servicio.ServicioAuth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "Login, logout y validación de tokens")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ServicioAuth servicioAuth;

    public AuthController(ServicioAuth servicioAuth) {
        this.servicioAuth = servicioAuth;
    }

    @Operation(summary = "Login", description = "Autentica un usuario y devuelve un token de sesión.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = servicioAuth.login(request.getUsuario(), request.getContrasena());
        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout", description = "Cierra la sesión invalidando el token.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String tokenLimpio = token.replace("Bearer ", "");
        servicioAuth.cerrarSesion(tokenLimpio);
        return ResponseEntity.ok("Sesión cerrada.");
    }

    @Operation(summary = "Validar token", description = "Verifica si un token de sesión es válido.")
    @GetMapping("/validar")
    public ResponseEntity<Boolean> validar(@RequestParam String token) {
        return ResponseEntity.ok(servicioAuth.validarToken(token));
    }
}
