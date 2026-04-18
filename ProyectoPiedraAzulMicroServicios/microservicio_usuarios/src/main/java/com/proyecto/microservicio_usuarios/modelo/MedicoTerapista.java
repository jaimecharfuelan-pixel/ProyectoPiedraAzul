package com.proyecto.microservicio_usuarios.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medico_terapista")
@PrimaryKeyJoinColumn(name = "id_persona")
public class MedicoTerapista extends Persona {

    @Column(name = "id_especialidad")
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
