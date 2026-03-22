package com.proyecto.logica.modelos;

import java.time.LocalDate;

public class Agendador extends Persona {

    public Agendador() {
        super();
    }

    public Agendador(int prmIdPersona, String prmNombre, String prmCedulaCiudadania, String prmApellido,
                     String prmCelular, Integer prmIdGenero, LocalDate prmFechaNacimiento,
                     String prmCorreo, Integer prmIdUsuario, Integer prmIdEstado) {
        
        super(prmIdPersona, prmNombre, prmCedulaCiudadania, prmApellido, prmCelular, prmIdGenero, 
              prmFechaNacimiento, prmCorreo, prmIdUsuario, prmIdEstado);
    }
}