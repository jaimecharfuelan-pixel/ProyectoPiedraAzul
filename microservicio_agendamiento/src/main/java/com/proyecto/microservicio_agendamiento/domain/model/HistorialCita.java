package com.proyecto.microservicio_agendamiento.domain.model;

import java.time.LocalDateTime;

/**
 * Entidad de dominio que registra el historial de cambios en una cita.
 * Cada cambio relevante (reagendación, cancelación, cambio de estado) genera un registro.
 */
public class HistorialCita {

    private int idHistorial;
    private int idCita;
    private String tipoCambio;           // "REAGENDAMIENTO", "CANCELACION", "CAMBIO_ESTADO", "CREACION"
    private String valorAnterior;        // Valor anterior (ej: fecha/hora original, estado anterior)
    private String valorNuevo;           // Valor nuevo (ej: fecha/hora nueva, estado nuevo)
    private LocalDateTime fechaHora;     // Cuándo ocurrió el cambio
    private int idUsuario;               // Quién hizo el cambio (id_persona del admin/médico/paciente)
    private String descripcion;          // Descripción adicional del cambio

    // Constructores
    public HistorialCita() {}

    public HistorialCita(int idCita, String tipoCambio, String valorAnterior, 
                        String valorNuevo, LocalDateTime fechaHora, int idUsuario, String descripcion) {
        this.idCita = idCita;
        this.tipoCambio = tipoCambio;
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
        this.fechaHora = fechaHora;
        this.idUsuario = idUsuario;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getIdHistorial() { return idHistorial; }
    public void setIdHistorial(int idHistorial) { this.idHistorial = idHistorial; }

    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public String getTipoCambio() { return tipoCambio; }
    public void setTipoCambio(String tipoCambio) { this.tipoCambio = tipoCambio; }

    public String getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }

    public String getValorNuevo() { return valorNuevo; }
    public void setValorNuevo(String valorNuevo) { this.valorNuevo = valorNuevo; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
