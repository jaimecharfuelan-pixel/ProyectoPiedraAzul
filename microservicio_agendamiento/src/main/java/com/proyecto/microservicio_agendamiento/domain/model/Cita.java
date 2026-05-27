package com.proyecto.microservicio_agendamiento.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Modelo de dominio de una cita médica. POJO puro, sin dependencias de infraestructura.
 * El estado de la cita se gestiona a través del patrón State (EstadoCita).
 */
public class Cita {

    private int idCita;
    private int idPaciente;
    private int idMedico;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    // Estado actual gestionado por el patrón State
    private EstadoCita estadoActual;

    public Cita() {}

    /** Constructor para citas nuevas — siempre inician en Pendiente. */
    public Cita(int idPaciente, int idMedico, LocalDate fecha,
                LocalTime horaInicio, LocalTime horaFin) {
        this.idPaciente   = idPaciente;
        this.idMedico     = idMedico;
        this.fecha        = fecha;
        this.horaInicio   = horaInicio;
        this.horaFin      = horaFin;
        this.estadoActual = FabricaEstadoCita.crearEstadoInicial();
    }

    /** Constructor para cargar desde persistencia con estado conocido. */
    public Cita(int idCita, int idPaciente, int idMedico, LocalDate fecha,
                LocalTime horaInicio, LocalTime horaFin, int idEstado) {
        this.idCita       = idCita;
        this.idPaciente   = idPaciente;
        this.idMedico     = idMedico;
        this.fecha        = fecha;
        this.horaInicio   = horaInicio;
        this.horaFin      = horaFin;
        this.estadoActual = FabricaEstadoCita.crearEstado(idEstado);
    }

    // ── Transiciones de estado (delegadas al patrón State) ────────────────────

    public boolean confirmar() {
        boolean ok = estadoActual.confirmar();
        if (ok) estadoActual = FabricaEstadoCita.crearEstado(EstadoCitaId.CONFIRMADA);
        return ok;
    }

    public boolean cancelar() {
        boolean ok = estadoActual.cancelar();
        if (ok) estadoActual = FabricaEstadoCita.crearEstado(EstadoCitaId.CANCELADA);
        return ok;
    }

    public boolean completar() {
        boolean ok = estadoActual.completar();
        if (ok) estadoActual = FabricaEstadoCita.crearEstado(EstadoCitaId.COMPLETADA);
        return ok;
    }

    public boolean marcarNoAsistio() {
        boolean ok = estadoActual.marcarNoAsistio();
        if (ok) estadoActual = FabricaEstadoCita.crearEstado(EstadoCitaId.NO_ASISTIO);
        return ok;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public int getIdEstadoCita() {
        return estadoActual != null ? estadoActual.obtenerID() : EstadoCitaId.PENDIENTE;
    }

    public void setIdEstadoCita(int idEstado) {
        this.estadoActual = FabricaEstadoCita.crearEstado(idEstado);
    }

    public String getNombreEstado() {
        return estadoActual != null ? estadoActual.obtenerNombre() : "Pendiente";
    }
}
