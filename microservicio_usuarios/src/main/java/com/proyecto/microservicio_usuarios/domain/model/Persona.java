package com.proyecto.microservicio_usuarios.domain.model;

import java.time.LocalDate;

public abstract class Persona {

    private int idPersona;
    private String nombre;
    private String cedulaCiudadania;
    private String apellido;
    private String celular;
    private Integer idGenero;
    private LocalDate fechaNacimiento;
    private String correo;
    private Integer idUsuario;
    private Integer idEstado;
    private boolean activo = true;

    public Persona() {}

    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCedulaCiudadania() { return cedulaCiudadania; }
    public void setCedulaCiudadania(String cedulaCiudadania) { this.cedulaCiudadania = cedulaCiudadania; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public Integer getIdGenero() { return idGenero; }
    public void setIdGenero(Integer idGenero) { this.idGenero = idGenero; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdEstado() { return idEstado; }
    public void setIdEstado(Integer idEstado) { this.idEstado = idEstado; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
