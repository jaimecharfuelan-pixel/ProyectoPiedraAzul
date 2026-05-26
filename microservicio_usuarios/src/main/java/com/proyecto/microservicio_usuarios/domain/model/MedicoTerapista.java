package com.proyecto.microservicio_usuarios.domain.model;

import java.time.LocalDate;

public class MedicoTerapista extends Persona {

    private int idEspecialidad;

    public MedicoTerapista() {
        super();
    }

    public MedicoTerapista(int idPersona, String nombre, String cedulaCiudadania, String apellido,
                           String celular, Integer idGenero, LocalDate fechaNacimiento,
                           String correo, Integer idUsuario, Integer idEstado, int idEspecialidad) {
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
        this.idEspecialidad = idEspecialidad;
    }

    public int getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(int idEspecialidad) { this.idEspecialidad = idEspecialidad; }
}
