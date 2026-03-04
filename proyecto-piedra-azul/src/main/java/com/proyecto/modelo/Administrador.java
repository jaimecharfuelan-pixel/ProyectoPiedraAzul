package com.proyecto.modelo;

public class Administrador extends Persona {
    private String usuario;
    private String clave;

    public Administrador() {
    }

    @Override
    public String getUsuario() {
        return usuario;
    }

    @Override
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @Override
    public String getClave() {
        return clave;
    }

    @Override
    public void setClave(String clave) {
        this.clave = clave;
    }

    @Override
    public String getRol() {
        return "Administrador";
    }
}
