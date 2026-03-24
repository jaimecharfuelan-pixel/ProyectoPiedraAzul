package com.proyecto.presentacion;

import com.proyecto.logica.modelos.Persona;

public class SesionUsuario {

    private static SesionUsuario attInstancia;

    private Persona attUsuarioActual;
    private String token;
    private int idUsuario;
    private String rol;

    private SesionUsuario() {
    }

    public static SesionUsuario getInstancia() {
        if (attInstancia == null) {
            attInstancia = new SesionUsuario();
        }
        return attInstancia;
    }

    public void setUsuario(Persona prmUsuario) {
        this.attUsuarioActual = prmUsuario;
    }

    public Persona getUsuario() {
        return attUsuarioActual;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void limpiarSesion() {
        this.attUsuarioActual = null;
        this.token = null;
        this.idUsuario = 0;
        this.rol = null;
    }
}