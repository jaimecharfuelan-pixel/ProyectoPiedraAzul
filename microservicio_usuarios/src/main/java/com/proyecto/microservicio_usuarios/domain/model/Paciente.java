package com.proyecto.microservicio_usuarios.domain.model;

import java.time.LocalDate;

public class Paciente extends Persona {

    public Paciente() {
        super();
    }

    public Paciente(int idPersona, String nombre, String cedulaCiudadania, String apellido,
                    String celular, Integer idGenero, LocalDate fechaNacimiento,
                    String correo, Integer idUsuario, Integer idEstado) {
        super();
        setIdPersona(idPersona);
        setNombre(nombre);
        setCedulaCiudadania(cedulaCiudadania);
        setApellido(apellido);
        setCelular(celular);
        setIdGenero(idGenero);
        setFechaNacimiento(fechaNacimiento);
        setCorreo(correo);
        setIdUsuario(idUsuario);
        setIdEstado(idEstado);
    }
}
