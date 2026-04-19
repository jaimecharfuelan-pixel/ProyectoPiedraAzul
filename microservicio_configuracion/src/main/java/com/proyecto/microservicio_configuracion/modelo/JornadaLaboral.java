package com.proyecto.microservicio_configuracion.modelo;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "jornada_laboral")
public class JornadaLaboral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jornada")
    private int idJornada;

    @Column(name = "dia_semana", nullable = false)
    private String diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "id_estado")
    private int idEstado;

    @Column(name = "id_usuario", nullable = false)
    private int idUsuario;

    public JornadaLaboral() {}

    public int getIdJornada() { return idJornada; }
    public void setIdJornada(int idJornada) { this.idJornada = idJornada; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public int getIdEstado() { return idEstado; }
    public void setIdEstado(int idEstado) { this.idEstado = idEstado; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
}
