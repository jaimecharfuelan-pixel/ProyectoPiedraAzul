package com.proyecto.microservicio_usuarios.application.dto;

import java.time.LocalDate;

/**
 * Comando para crear una persona desde el panel de administración.
 * Crea usuario + persona + rol en una sola operación.
 */
public class CrearPersonaCommand {

    private String nombre;
    private String apellido;
    private String cedulaCiudadania;
    private String celular;
    private String correo;
    private Integer idGenero;
    private LocalDate fechaNacimiento;
    private String usuarioLogin;
    private String contrasena;
    private String rol;

    public CrearPersonaCommand() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCedulaCiudadania() { return cedulaCiudadania; }
    public void setCedulaCiudadania(String cedulaCiudadania) { this.cedulaCiudadania = cedulaCiudadania; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Integer getIdGenero() { return idGenero; }
    public void setIdGenero(Integer idGenero) { this.idGenero = idGenero; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getUsuarioLogin() { return usuarioLogin; }
    public void setUsuarioLogin(String usuarioLogin) { this.usuarioLogin = usuarioLogin; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
