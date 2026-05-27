package com.proyecto.presentacion.dto;

import java.time.LocalDateTime;

/**
 * DTO para transmitir información de historial de citas al frontend.
 */
public class HistorialCitaDTO {
    private int idHistorial;
    private int idCita;
    private String tipoCambio;
    private String valorAnterior;
    private String valorNuevo;
    private LocalDateTime fechaHora;
    private int idUsuario;
    private String descripcion;

    // Constructores
    public HistorialCitaDTO() {}

    public HistorialCitaDTO(int idCita, String tipoCambio, String valorAnterior, String valorNuevo,
                           LocalDateTime fechaHora, int idUsuario, String descripcion) {
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

    @Override
    public String toString() {
        return String.format("[%s] %s → %s (Usuario: %d, %s)",
                tipoCambio, valorAnterior, valorNuevo, idUsuario, fechaHora);
    }
}
