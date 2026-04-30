package com.proyecto.microservicio_agendamiento.dto;

/**
 * DTO ligero recibido desde ms-usuarios via HTTP.
 * Solo contiene los campos que ms-agendamiento necesita para asociar una cita.
 */
public class PacienteResumenDTO {

    private int idPaciente;
    private String nombre;
    private String apellido;

    public PacienteResumenDTO() {}

    public PacienteResumenDTO(int idPaciente, String nombre, String apellido) {
        this.idPaciente = idPaciente;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
}
