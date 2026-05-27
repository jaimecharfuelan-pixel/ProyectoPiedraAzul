package com.proyecto.microservicio_agendamiento.domain.model;

/**
 * Factory para instanciar estados de cita por su ID.
 * Centraliza la creación y evita switch dispersos en el código.
 */
public final class FabricaEstadoCita {

    private FabricaEstadoCita() {}

    public static EstadoCita crearEstado(int idEstado) {
        return switch (idEstado) {
            case EstadoCitaId.CANCELADA  -> new EstadoCancelada();
            case EstadoCitaId.PENDIENTE  -> new EstadoPendiente();
            case EstadoCitaId.CONFIRMADA -> new EstadoConfirmada();
            case EstadoCitaId.COMPLETADA -> new EstadoCompletada();
            case EstadoCitaId.NO_ASISTIO -> new EstadoNoAsistio();
            default -> throw new IllegalArgumentException("ID de estado inválido: " + idEstado);
        };
    }

    public static EstadoCita crearEstadoInicial() {
        return new EstadoPendiente();
    }
}
