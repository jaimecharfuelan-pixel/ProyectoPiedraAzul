package com.proyecto.logica.modelos;

import java.time.LocalDate;

public class Administrador extends Persona {
    public Administrador(String prmDocumento, String prmNombre, String prmApellido, String prmCelular, String prmGenero,
            LocalDate prmFechaNacimiento, String prmEmail) {
        super(prmDocumento, prmNombre, prmApellido, prmCelular, prmGenero, prmFechaNacimiento, prmEmail);
    }
}
