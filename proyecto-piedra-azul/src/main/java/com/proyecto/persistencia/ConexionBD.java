package com.proyecto.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement; // Importante añadir esta importación

public class ConexionBD {
    private static Connection instancia;
    private static final String URL = "jdbc:postgresql://localhost:5432/BaseDeDatosPiedraAzul";
    private static final String USUARIO = "PiedraAzulUsuario";
    private static final String CONTRASENA = "PiedraAzulContrasena";

    private ConexionBD() {
    } // Constructor privado (GoF: Singleton)

    public static Connection getInstance() throws SQLException {
        if (instancia == null || instancia.isClosed()) {
            instancia = DriverManager.getConnection(URL, USUARIO, CONTRASENA);

            // --- CAMBIO CLAVE AQUÍ ---
            // Configuramos el esquema por defecto para esta sesión
            try (Statement stmt = instancia.createStatement()) {
                stmt.execute("SET search_path TO esquemaprueba, public");
            }
            // -------------------------

            System.out.println("Conexión exitosa a PostgreSQL (Esquema: esquemaprueba)");
        }
        return instancia;
    }
}