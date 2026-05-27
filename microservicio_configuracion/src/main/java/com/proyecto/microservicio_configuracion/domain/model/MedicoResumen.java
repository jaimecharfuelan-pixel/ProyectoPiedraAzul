package com.proyecto.microservicio_configuracion.domain.model;

/**
 * Modelo de dominio ligero para representar un médico activo.
 * Recibido desde ms-usuarios. Solo contiene lo que ms-configuracion necesita.
 */
public class MedicoResumen {

    private int idMedico;
    private String nombre;
    private String apellido;

    public MedicoResumen() {}

    public MedicoResumen(int idMedico, String nombre, String apellido) {
        this.idMedico  = idMedico;
        this.nombre    = nombre;
        this.apellido  = apellido;
    }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
}
