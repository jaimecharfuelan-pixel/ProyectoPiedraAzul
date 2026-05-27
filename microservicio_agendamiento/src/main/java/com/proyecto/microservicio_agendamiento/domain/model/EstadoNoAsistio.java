package com.proyecto.microservicio_agendamiento.domain.model;

public class EstadoNoAsistio implements EstadoCita {

    @Override public boolean confirmar()      { return false; }
    @Override public boolean cancelar()       { return false; }
    @Override public boolean completar()      { return false; }
    @Override public boolean marcarNoAsistio(){ return true; }  // idempotente
    @Override public String obtenerNombre()   { return "No Asistió"; }
    @Override public int obtenerID()          { return EstadoCitaId.NO_ASISTIO; }
}
