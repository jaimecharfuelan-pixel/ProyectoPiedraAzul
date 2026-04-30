package com.proyecto.microservicio_usuarios.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "agendador")
@PrimaryKeyJoinColumn(name = "id_persona")
public class Agendador extends Persona {

    public Agendador() {
        super();
    }

    public Agendador(int idPersona, String nombre, String cedulaCiudadania, String apellido,
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
