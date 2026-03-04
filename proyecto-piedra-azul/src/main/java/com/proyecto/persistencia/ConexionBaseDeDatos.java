package com.proyecto.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBaseDeDatos {
    private static Connection instancia;
    private static final String URL = "jdbc:postgresql://localhost:5432/BaseDeDatosPiedraAzul";
    private static final String USUARIO = "PiedraAzulUsuario";
    private static final String CONTRASENA = "PiedraAzulContraseña";

    private ConexionBaseDeDatos() {} // Constructor privado (GoF: Singleton)

    public static Connection getInstance() throws SQLException {
        if (instancia == null || instancia.isClosed()) {
            instancia = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("Conexión exitosa a PostgreSQL");
        }
        return instancia;
    }
}