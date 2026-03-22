package com.proyecto.presentacion;

import com.proyecto.logica.modelos.Persona;

public class SesionUsuario {

    private static SesionUsuario attInstancia;

    private Persona attUsuarioActual;
    private String token;

    private SesionUsuario() {
    }

    public static SesionUsuario getInstancia() {
        if (attInstancia == null) {
            attInstancia = new SesionUsuario();
        }
        return attInstancia;
    }

    // 🔹 Usuario
    public void setUsuario(Persona prmUsuario) {
        this.attUsuarioActual = prmUsuario;
    }

    public Persona getUsuario() {
        return attUsuarioActual;
    }

    // TOKEN (LO NUEVO)
    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    // OPCIONAL: cerrar sesión
    public void limpiarSesion() {
        this.attUsuarioActual = null;
        this.token = null;
    }
}