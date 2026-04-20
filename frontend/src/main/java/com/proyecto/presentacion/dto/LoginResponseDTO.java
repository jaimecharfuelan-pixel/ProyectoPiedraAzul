package com.proyecto.presentacion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponseDTO {
    private String token;
    private int    idUsuario;
    private String usuario;
    private String rol;

    public LoginResponseDTO() {}

    public String getToken()              { return token; }
    public void   setToken(String v)      { this.token = v; }
    public int    getIdUsuario()          { return idUsuario; }
    public void   setIdUsuario(int v)     { this.idUsuario = v; }
    public String getUsuario()            { return usuario; }
    public void   setUsuario(String v)    { this.usuario = v; }
    public String getRol()                { return rol; }
    public void   setRol(String v)        { this.rol = v; }
}
