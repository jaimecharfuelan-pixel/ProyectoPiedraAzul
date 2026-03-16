package com.proyecto.logica.modelos;

import java.time.LocalDate;

public class Agendador extends Persona {

    public Agendador(int id, String documento, String nombre, String apellido,
            String celular, String genero,
            LocalDate fechaNacimiento, String email) {

        super(id, documento, nombre, apellido, celular, genero, fechaNacimiento, email);
    }

}