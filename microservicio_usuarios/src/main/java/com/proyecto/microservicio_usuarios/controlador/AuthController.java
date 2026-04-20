package com.proyecto.microservicio_usuarios.controlador;

import com.proyecto.microservicio_usuarios.dto.LoginRequestDTO;
import com.proyecto.microservicio_usuarios.dto.LoginResponseDTO;
import com.proyecto.microservicio_usuarios.servicio.ServicioAuth;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ServicioAuth servicioAuth;

    public AuthController(ServicioAuth servicioAuth) {
        this.servicioAuth = servicioAuth;
    }

    /** POST /api/auth/login */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = servicioAuth.login(request.getUsuario(), request.getContrasena());
        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
        }
        return ResponseEntity.ok(response);
    }

    /** POST /api/auth/logout */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String tokenLimpio = token.replace("Bearer ", "");
        servicioAuth.cerrarSesion(tokenLimpio);
        return ResponseEntity.ok("Sesión cerrada.");
    }

    /** GET /api/auth/validar?token=xxx */
    @GetMapping("/validar")
    public ResponseEntity<Boolean> validar(@RequestParam String token) {
        return ResponseEntity.ok(servicioAuth.validarToken(token));
    }
}
