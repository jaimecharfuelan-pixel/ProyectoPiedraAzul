package com.proyecto.logica.modelos;

import java.time.LocalDate;

public class MedicoTerapista extends Persona {

    private int especialidad;

    public MedicoTerapista(int id, String documento, String nombre, String apellido,
            String celular, String genero,
            LocalDate fechaNacimiento, String email,
            int especialidad) {

        super(id, documento, nombre, apellido, celular, genero, fechaNacimiento, email);
        this.especialidad = especialidad;
    }

    public int getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(int especialidad) {
        this.especialidad = especialidad;
    }
}