package com.proyecto.microservicio_agendamiento.domain.model;

public class EstadoCancelada implements EstadoCita {

    @Override public boolean confirmar()      { return false; }
    @Override public boolean cancelar()       { return true; }  // idempotente
    @Override public boolean completar()      { return false; }
    @Override public boolean marcarNoAsistio(){ return false; }
    @Override public String obtenerNombre()   { return "Cancelada"; }
    @Override public int obtenerID()          { return EstadoCitaId.CANCELADA; }
}
