package com.proyecto.microservicio_agendamiento.domain.model;

public class EstadoPendiente implements EstadoConsulta {

    @Override public boolean confirmar()      { return true; }
    @Override public boolean cancelar()       { return true; }
    @Override public boolean completar()      { return false; }
    @Override public boolean marcarNoAsistio(){ return false; }
    @Override public String obtenerNombre()   { return "Pendiente"; }
    @Override public int obtenerID()          { return EstadoCitaId.PENDIENTE; }
}
