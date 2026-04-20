package com.proyecto.microservicio_usuarios.dto;

public class LoginResponseDTO {
    private String token;
    private int idUsuario;
    private String usuario;
    private String rol;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, int idUsuario, String usuario, String rol) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.rol = rol;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
