package com.proyecto.microservicio_usuarios.dto;

import java.time.LocalDate;

/**
 * DTO para crear una persona desde el panel de administración.
 * Crea usuario + persona + rol en una sola llamada.
 */
public class CrearPersonaAdminDTO {

    private String    nombre;
    private String    apellido;
    private String    cedulaCiudadania;
    private String    celular;
    private String    correo;
    private Integer   idGenero;
    private LocalDate fechaNacimiento;
    // Credenciales del usuario a crear
    private String    usuarioLogin;
    private String    contrasena;
    // Rol a asignar (Paciente, Medico, Agendador, etc.)
    private String    rol;

    public CrearPersonaAdminDTO() {}

    public String    getNombre()             { return nombre; }
    public void      setNombre(String v)     { this.nombre = v; }
    public String    getApellido()           { return apellido; }
    public void      setApellido(String v)   { this.apellido = v; }
    public String    getCedulaCiudadania()   { return cedulaCiudadania; }
    public void      setCedulaCiudadania(String v) { this.cedulaCiudadania = v; }
    public String    getCelular()            { return celular; }
    public void      setCelular(String v)    { this.celular = v; }
    public String    getCorreo()             { return correo; }
    public void      setCorreo(String v)     { this.correo = v; }
    public Integer   getIdGenero()           { return idGenero; }
    public void      setIdGenero(Integer v)  { this.idGenero = v; }
    public LocalDate getFechaNacimiento()    { return fechaNacimiento; }
    public void      setFechaNacimiento(LocalDate v) { this.fechaNacimiento = v; }
    public String    getUsuarioLogin()       { return usuarioLogin; }
    public void      setUsuarioLogin(String v) { this.usuarioLogin = v; }
    public String    getContrasena()         { return contrasena; }
    public void      setContrasena(String v) { this.contrasena = v; }
    public String    getRol()                { return rol; }
    public void      setRol(String v)        { this.rol = v; }
}
