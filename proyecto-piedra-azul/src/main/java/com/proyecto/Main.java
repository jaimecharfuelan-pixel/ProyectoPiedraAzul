package com.proyecto;

import java.sql.Connection;
import java.sql.SQLException;
import com.proyecto.persistencia.ConexionBaseDeDatos; // Asegúrate que el nombre coincida

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Iniciando Aplicación Piedra Azul ---");

        try {
            // Intentamos obtener la instancia única de la conexión (Singleton)
            Connection conexion = ConexionBaseDeDatos.getInstance();
            
            if (conexion != null && !conexion.isClosed()) {
                System.out.println("✅ OK: La conexión a PostgreSQL funciona correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("❌ ERROR: No se pudo conectar a la base de datos.");
            System.err.println("Detalle: " + e.getMessage());
            System.err.println("¿Está el contenedor Docker encendido? (docker-compose up -d)");
        }
    }
}