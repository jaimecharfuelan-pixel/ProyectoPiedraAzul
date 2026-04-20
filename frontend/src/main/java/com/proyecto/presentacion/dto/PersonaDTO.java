package com.proyecto.presentacion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonaDTO {
    private int       idPersona;
    private String    nombre;
    private String    apellido;
    private String    cedulaCiudadania;
    private String    celular;
    private String    correo;
    private Integer   idGenero;
    private LocalDate fechaNacimiento;
    private Integer   idUsuario;
    private Integer   idEstado;

    public PersonaDTO() {}

    public int       getIdPersona()                  { return idPersona; }
    public void      setIdPersona(int v)             { this.idPersona = v; }
    public String    getNombre()                     { return nombre; }
    public void      setNombre(String v)             { this.nombre = v; }
    public String    getApellido()                   { return apellido; }
    public void      setApellido(String v)           { this.apellido = v; }
    public String    getCedulaCiudadania()           { return cedulaCiudadania; }
    public void      setCedulaCiudadania(String v)   { this.cedulaCiudadania = v; }
    public String    getCelular()                    { return celular; }
    public void      setCelular(String v)            { this.celular = v; }
    public String    getCorreo()                     { return correo; }
    public void      setCorreo(String v)             { this.correo = v; }
    public Integer   getIdGenero()                   { return idGenero; }
    public void      setIdGenero(Integer v)          { this.idGenero = v; }
    public LocalDate getFechaNacimiento()            { return fechaNacimiento; }
    public void      setFechaNacimiento(LocalDate v) { this.fechaNacimiento = v; }
    public Integer   getIdUsuario()                  { return idUsuario; }
    public void      setIdUsuario(Integer v)         { this.idUsuario = v; }
    public Integer   getIdEstado()                   { return idEstado; }
    public void      setIdEstado(Integer v)          { this.idEstado = v; }

    @Override
    public String toString() { return nombre + " " + apellido; }
}
