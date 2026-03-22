package com.proyecto.logica.modelos;

import java.time.LocalDate;

public class MedicoTerapista extends Persona {
     
    private int attIdEspecialidad;

    public MedicoTerapista(int prmIdPersona, String prmNombre, String prmCedulaCiudadania,
                           String prmApellido, String prmCelular, Integer prmIdGenero,
                           LocalDate prmFechaNacimiento, String prmCorreo,
                           Integer prmIdUsuario, Integer prmIdEstado,
                           int prmIdEspecialidad) {

        super(prmIdPersona, prmNombre, prmCedulaCiudadania, prmApellido,
              prmCelular, prmIdGenero, prmFechaNacimiento,
              prmCorreo, prmIdUsuario, prmIdEstado);

        this.attIdEspecialidad = prmIdEspecialidad;
    }

    public int getIdEspecialidad() {
        return attIdEspecialidad;
    }

    public void setIdEspecialidad(int prmIdEspecialidad) {
        this.attIdEspecialidad = prmIdEspecialidad;
    }
}