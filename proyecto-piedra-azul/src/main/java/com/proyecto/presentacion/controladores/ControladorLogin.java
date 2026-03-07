package com.proyecto.presentacion.controladores;

import com.proyecto.logica.interfaces.IServicioUsuarios;

public class ControladorLogin {
    private IServicioUsuarios attServicioUsuarios;

    public ControladorLogin(IServicioUsuarios prmServicio) {
        this.attServicioUsuarios = prmServicio;
    }

    public void iniciarSesion(String prmUsuario, String prmClave) {
        // Lógica de inicio de sesión llamando al servicio
        // attServicioUsuarios.autenticar(prmUsuario, prmClave);
    }
}
