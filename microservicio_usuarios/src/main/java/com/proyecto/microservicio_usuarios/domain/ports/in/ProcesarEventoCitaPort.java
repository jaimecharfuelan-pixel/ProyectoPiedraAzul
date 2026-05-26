package com.proyecto.microservicio_usuarios.domain.ports.in;

public interface ProcesarEventoCitaPort {
    void procesarCitaCreada(int idCita, int idPaciente, int idMedico);
    void procesarCitaCancelada(int idCita);
}
