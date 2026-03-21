package com.proyecto.logica.modelos;

import java.time.LocalDate;

public class Medico extends Persona {
    private String attEspecialidad;

    public Medico(int prmId, String prmDocumento, String prmNombre, String prmApellido, String prmCelular,
            String prmGenero,
            LocalDate prmFechaNacimiento, String prmEmail, String prmEspecialidad) {
        super(prmId, prmDocumento, prmNombre, prmApellido, prmCelular, prmGenero, prmFechaNacimiento, prmEmail);
        this.attEspecialidad = prmEspecialidad;
    }

    public String getEspecialidad() {
        return attEspecialidad;
    }

    public void setEspecialidad(String prmEspecialidad) {
        this.attEspecialidad = prmEspecialidad;
    }
}
