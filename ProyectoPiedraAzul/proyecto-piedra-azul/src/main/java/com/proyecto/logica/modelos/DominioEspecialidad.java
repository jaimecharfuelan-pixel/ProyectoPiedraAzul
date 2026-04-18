package com.proyecto.logica.modelos;

public class DominioEspecialidad {
    
    private int attIdEspecialidad;
    private String attNombre;

    // Constructor vacío
    public DominioEspecialidad() {}

    // Constructor completo
    public DominioEspecialidad(int prmIdEspecialidad, String prmNombre) {
        this.attIdEspecialidad = prmIdEspecialidad;
        this.attNombre = prmNombre;
    }

    // Getters y Setters
    public int getIdEspecialidad() {
        return attIdEspecialidad;
    }

    public void setIdEspecialidad(int prmIdEspecialidad) {
        this.attIdEspecialidad = prmIdEspecialidad;
    }

    public String getNombre() {
        return attNombre;
    }

    public void setNombre(String prmNombre) {
        this.attNombre = prmNombre;
    }
}