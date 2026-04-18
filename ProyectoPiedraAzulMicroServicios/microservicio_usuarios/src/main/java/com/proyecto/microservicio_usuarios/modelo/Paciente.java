package com.proyecto.microservicio_usuarios.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "paciente")
@PrimaryKeyJoinColumn(name = "id_persona")
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
