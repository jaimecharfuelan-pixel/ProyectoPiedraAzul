package com.proyecto.microservicio_agendamiento.domain.model;

public class EstadoCompletada implements EstadoConsulta {

    @Override public boolean confirmar()      { return false; }
    @Override public boolean cancelar()       { return false; }
    @Override public boolean completar()      { return true; }  // idempotente
    @Override public boolean marcarNoAsistio(){ return false; }
    @Override public String obtenerNombre()   { return "Completada"; }
    @Override public int obtenerID()          { return EstadoCitaId.COMPLETADA; }
}
