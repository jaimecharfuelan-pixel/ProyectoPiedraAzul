package com.proyecto.microservicio_configuracion.dto;

/**
 * DTO ligero recibido desde ms-usuarios via HTTP.
 * Solo contiene los campos que ms-configuracion necesita para mostrar el médico al asignar jornadas.
 */
public class MedicoResumenDTO {

    private int idMedico;
    private String nombre;
    private String apellido;

    public MedicoResumenDTO() {}

    public MedicoResumenDTO(int idMedico, String nombre, String apellido) {
        this.idMedico = idMedico;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
}
