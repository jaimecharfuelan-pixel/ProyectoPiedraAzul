package com.proyecto.microservicio_agendamiento.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

//Builder abstracto para crear las citas

public abstract class CitaBuilder {

    protected int       idPaciente;
    protected int       idMedico;
    protected LocalDate fecha;
    protected LocalTime horaInicio;
    protected LocalTime horaFin;
    protected Integer   idEstadoCita;

    public CitaBuilder idPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
        return this;
    }

    public CitaBuilder idMedico(int idMedico) {
        this.idMedico = idMedico;
        return this;
    }

    public CitaBuilder fecha(LocalDate fecha) {
        this.fecha = fecha;
        return this;
    }

    public CitaBuilder horaInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
        return this;
    }

    public CitaBuilder horaFin(LocalTime horaFin) {
        this.horaFin = horaFin;
        return this;
    }

    public CitaBuilder idEstadoCita(Integer idEstadoCita) {
        this.idEstadoCita = idEstadoCita;
        return this;
    }

    /**
     * Método abstracto que cada subclase implementa para
     * aplicar sus reglas específicas antes de construir la cita.
     */
    protected abstract void aplicarReglas();

    /**
     * Construye y devuelve la Cita con los valores configurados.
     * Llama a aplicarReglas() antes de construir — garantiza
     * que cada tipo de cita cumpla sus propias validaciones.
     */
    public Cita build() {
        aplicarReglas();
        Cita cita = new Cita();
        cita.setIdPaciente(idPaciente);
        cita.setIdMedico(idMedico);
        cita.setFecha(fecha);
        cita.setHoraInicio(horaInicio);
        cita.setHoraFin(horaFin);
        cita.setIdEstadoCita(idEstadoCita);
        return cita;
    }
}
