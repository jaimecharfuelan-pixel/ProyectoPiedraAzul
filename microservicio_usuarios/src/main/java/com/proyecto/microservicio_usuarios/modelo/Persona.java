package com.proyecto.microservicio_usuarios.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "persona")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private int idPersona;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "cedula_ciudadania", nullable = false, unique = true)
    private String cedulaCiudadania;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "celular")
    private String celular;

    @Column(name = "id_genero")
    private Integer idGenero;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "correo")
    private String correo;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_estado")
    private Integer idEstado;

    /** true = activo, false = inactivo. Columna propia, independiente de dominio_estado. */
    @Column(name = "activo", nullable = false, columnDefinition = "boolean default true")
    private boolean activo = true;

    public Persona() {}

    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCedulaCiudadania() { return cedulaCiudadania; }
    public void setCedulaCiudadania(String cedulaCiudadania) { this.cedulaCiudadania = cedulaCiudadania; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public Integer getIdGenero() { return idGenero; }
    public void setIdGenero(Integer idGenero) { this.idGenero = idGenero; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdEstado() { return idEstado; }
    public void setIdEstado(Integer idEstado) { this.idEstado = idEstado; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
