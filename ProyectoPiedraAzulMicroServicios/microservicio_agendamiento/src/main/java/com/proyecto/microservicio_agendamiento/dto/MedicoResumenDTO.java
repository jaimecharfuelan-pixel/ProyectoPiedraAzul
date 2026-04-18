package com.proyecto.microservicio_agendamiento.dto;

/**
 * DTO ligero recibido desde ms-usuarios via HTTP.
 * Solo contiene los campos que ms-agendamiento necesita para operar.
 */
public class MedicoResumenDTO {

    private int idMedico;
    private String nombre;
    private String apellido;
    private String especialidad;

    public MedicoResumenDTO() {}

    public MedicoResumenDTO(int idMedico, String nombre, String apellido, String especialidad) {
        this.idMedico = idMedico;
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
    }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}
