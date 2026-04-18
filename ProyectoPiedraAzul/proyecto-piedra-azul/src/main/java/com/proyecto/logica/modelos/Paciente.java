package com.proyecto.logica.modelos;

import java.time.LocalDate;

public class Paciente extends Persona {


    // Constructor vacío
    public Paciente() {
        super();
    }

    // Constructor completo
    public Paciente(int prmIdPersona, String prmNombre, String prmCedulaCiudadania, String prmApellido,
                    String prmCelular, Integer prmIdGenero, LocalDate prmFechaNacimiento,
                    String prmCorreo, Integer prmIdUsuario, Integer prmIdEstado,
                    Integer prmIdAgendadorContacto) {

        super(prmIdPersona, prmNombre, prmCedulaCiudadania, prmApellido, prmCelular, prmIdGenero,
              prmFechaNacimiento, prmCorreo, prmIdUsuario, prmIdEstado);

    }


    
}