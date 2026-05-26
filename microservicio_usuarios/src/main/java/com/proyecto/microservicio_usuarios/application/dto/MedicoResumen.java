package com.proyecto.microservicio_usuarios.application.dto;

/**
 * DTO de salida para médicos activos. Usado por ms-configuracion y ms-agendamiento.
 */
public class MedicoResumen {

    private int idMedico;
    private String nombre;
    private String apellido;
    private int idEspecialidad;

    public MedicoResumen() {}

    public MedicoResumen(int idMedico, String nombre, String apellido, int idEspecialidad) {
        this.idMedico = idMedico;
        this.nombre = nombre;
        this.apellido = apellido;
        this.idEspecialidad = idEspecialidad;
    }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public int getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(int idEspecialidad) { this.idEspecialidad = idEspecialidad; }
}
