package com.proyecto.logica.modelos;

import java.time.LocalDate;

public class Paciente extends Persona {
    public Paciente(String prmDocumento, String prmNombre, String prmApellido, String prmCelular, String prmGenero,
            LocalDate prmFechaNacimiento, String prmEmail) {
        super(prmDocumento, prmNombre, prmApellido, prmCelular, prmGenero, prmFechaNacimiento, prmEmail);
    }
}
