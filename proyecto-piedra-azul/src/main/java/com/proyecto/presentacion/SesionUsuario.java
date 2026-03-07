package com.proyecto.presentacion;

import com.proyecto.logica.modelos.Persona;

public class SesionUsuario {
    private static SesionUsuario attInstancia;
    private Persona attUsuarioActual;

    private SesionUsuario() {
        // Constructor privado para Singleton
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
}
