package com.proyecto.logica.modelos;

import java.time.LocalDate;

public class Administrador extends Persona {
    public Administrador(int prmId, String prmDocumento, String prmNombre, String prmApellido, String prmCelular,
            String prmGenero,
            LocalDate prmFechaNacimiento, String prmEmail) {
        super(prmId, prmDocumento, prmNombre, prmApellido, prmCelular, prmGenero, prmFechaNacimiento, prmEmail);
    }
}
