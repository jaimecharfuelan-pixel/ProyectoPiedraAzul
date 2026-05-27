package com.proyecto.microservicio_agendamiento.application.usecase;

import com.proyecto.microservicio_agendamiento.domain.model.HistorialCita;
import com.proyecto.microservicio_agendamiento.domain.ports.out.HistorialCitaRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de uso para registrar cambios en el historial de citas.
 * Se invoca automáticamente cuando una cita es reagendada, cancelada o cambia de estado.
 */
@Service
public class RegistrarHistorialCitaUseCase {

    private final HistorialCitaRepositoryPort historialRepo;

    public RegistrarHistorialCitaUseCase(HistorialCitaRepositoryPort historialRepo) {
        this.historialRepo = historialRepo;
    }

    /**
     * Registra un cambio en el historial de una cita.
     * 
     * @param idCita ID de la cita
     * @param tipoCambio Tipo de cambio: "REAGENDAMIENTO", "CANCELACION", "CAMBIO_ESTADO", "CREACION"
     * @param valorAnterior Valor anterior (estado, fecha, hora, etc.)
     * @param valorNuevo Valor nuevo (estado, fecha, hora, etc.)
     * @param idUsuario ID del usuario que realizó el cambio
     * @param descripcion Descripción adicional del cambio
     */
    public void registrarCambio(int idCita, String tipoCambio, String valorAnterior, 
                                String valorNuevo, int idUsuario, String descripcion) {
        HistorialCita historial = new HistorialCita(
                idCita,
                tipoCambio,
                valorAnterior,
                valorNuevo,
                LocalDateTime.now(),
                idUsuario,
                descripcion
        );
        historialRepo.save(historial);
    }

    /**
     * Obtiene el historial completo de cambios de una cita.
     */
    public List<HistorialCita> obtenerHistorialCita(int idCita) {
        return historialRepo.findByCitaId(idCita);
    }
}
