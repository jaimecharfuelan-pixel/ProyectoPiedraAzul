package com.proyecto.microservicio_configuracion.application.usecase;

import com.proyecto.microservicio_configuracion.domain.model.JornadaLaboral;
import com.proyecto.microservicio_configuracion.domain.ports.in.GestionarJornadaPort;
import com.proyecto.microservicio_configuracion.domain.ports.out.JornadaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GestionarJornadaUseCase implements GestionarJornadaPort {

    private static final int DURACION_DEFAULT_MINUTOS = 30;

    private final JornadaRepositoryPort jornadaRepo;

    public GestionarJornadaUseCase(JornadaRepositoryPort jornadaRepo) {
        this.jornadaRepo = jornadaRepo;
    }

    @Override
    public JornadaLaboral crear(JornadaLaboral jornada) {
        validarHorarios(jornada);
        if (jornada.getDuracionEstimadaAtencion() <= 0) {
            jornada.setDuracionEstimadaAtencion(DURACION_DEFAULT_MINUTOS);
        }
        return jornadaRepo.save(jornada);
    }

    @Override
    public JornadaLaboral editar(int idJornada, JornadaLaboral jornada) {
        if (!jornadaRepo.existsById(idJornada)) {
            throw new IllegalArgumentException("Jornada no encontrada: " + idJornada);
        }
        validarHorarios(jornada);
        jornada.setIdJornada(idJornada);
        return jornadaRepo.save(jornada);
    }

    @Override
    public boolean eliminar(int idJornada) {
        if (!jornadaRepo.existsById(idJornada)) return false;
        jornadaRepo.deleteById(idJornada);
        return true;
    }

    @Override
    public List<JornadaLaboral> listarTodas() {
        return jornadaRepo.findAll();
    }

    @Override
    public List<JornadaLaboral> listarPorMedico(int idMedico) {
        return jornadaRepo.findByMedico(idMedico);
    }

    @Override
    public List<String> listarDiasConJornada(int idMedico) {
        return jornadaRepo.findByMedico(idMedico).stream()
                .map(JornadaLaboral::getDiaSemana)
                .distinct()
                .toList();
    }

    @Override
    public Optional<JornadaLaboral> buscarPorMedicoYDia(int idMedico, String diaSemana) {
        return jornadaRepo.findByMedicoYDia(idMedico, diaSemana).stream().findFirst();
    }

    // ── Validaciones de dominio ───────────────────────────────────────────────

    private void validarHorarios(JornadaLaboral jornada) {
        if (jornada.getHoraInicio() == null || jornada.getHoraFin() == null) {
            throw new IllegalArgumentException("Hora de inicio y fin son obligatorias.");
        }
        if (jornada.getHoraInicio().isAfter(jornada.getHoraFin())) {
            throw new IllegalArgumentException("La hora de inicio no puede ser posterior a la de fin.");
        }
    }
}
