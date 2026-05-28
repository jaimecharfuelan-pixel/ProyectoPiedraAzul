package com.proyecto.microservicio_usuarios.infrastructure.adapters.in.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String mensaje = ex.getMostSpecificCause().getMessage();
        if (mensaje != null && mensaje.contains("cedula_ciudadania")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La cédula ya existe.");
        }
        if (mensaje != null && mensaje.contains("usuario")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre de usuario ya existe.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de validación de datos.");
    }
}
