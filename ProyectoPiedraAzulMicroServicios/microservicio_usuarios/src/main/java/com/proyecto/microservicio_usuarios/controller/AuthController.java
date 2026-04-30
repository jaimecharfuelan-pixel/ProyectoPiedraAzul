package com.proyecto.microservicio_usuarios.controller;

import com.proyecto.microservicio_usuarios.servicio.ServicioAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ServicioAuth servicioAuth;

    public AuthController(ServicioAuth servicioAuth) {
        this.servicioAuth = servicioAuth;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String usuario = body.get("usuario");
        String contrasena = body.get("contrasena");

        String token = servicioAuth.login(usuario, contrasena);

        if (token == null) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }

        return ResponseEntity.ok(Map.of("token", token));
    }
}