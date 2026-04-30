package com.proyecto.microservicio_usuarios.dto;

/**
 * DTO ligero de médico expuesto a otros microservicios (ms-configuracion, ms-agendamiento).
 */
public class MedicoResumenDTO {

    private int idMedico;
    private String nombre;
    private String apellido;
    private int idEspecialidad;

    public MedicoResumenDTO() {}

    public MedicoResumenDTO(int idMedico, String nombre, String apellido, int idEspecialidad) {
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
