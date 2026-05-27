package com.proyecto.microservicio_agendamiento.domain.model;

public class EstadoConfirmada implements EstadoCita {

    @Override public boolean confirmar()      { return true; }
    @Override public boolean cancelar()       { return true; }
    @Override public boolean completar()      { return true; }
    @Override public boolean marcarNoAsistio(){ return true; }
    @Override public String obtenerNombre()   { return "Confirmada"; }
    @Override public int obtenerID()          { return EstadoCitaId.CONFIRMADA; }
}
