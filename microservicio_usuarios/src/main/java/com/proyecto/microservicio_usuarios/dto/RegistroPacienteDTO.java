package com.proyecto.microservicio_usuarios.dto;

import com.proyecto.microservicio_usuarios.modelo.Paciente;
import com.proyecto.microservicio_usuarios.modelo.Usuario;

/**
 * DTO para RF2 y RF3: registrar paciente con su usuario en una sola llamada.
 */
public class RegistroPacienteDTO {

    private Paciente paciente;
    private Usuario usuario;

    public RegistroPacienteDTO() {}

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
