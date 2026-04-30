package com.proyecto.presentacion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicoDTO {
    private int    idMedico;
    private String nombre;
    private String apellido;
    private int    idEspecialidad;

    public MedicoDTO() {}

    public int    getIdMedico()              { return idMedico; }
    public void   setIdMedico(int v)         { this.idMedico = v; }
    public String getNombre()                { return nombre; }
    public void   setNombre(String v)        { this.nombre = v; }
    public String getApellido()              { return apellido; }
    public void   setApellido(String v)      { this.apellido = v; }
    public int    getIdEspecialidad()        { return idEspecialidad; }
    public void   setIdEspecialidad(int v)   { this.idEspecialidad = v; }

    @Override
    public String toString() { return nombre + " " + apellido; }
}
