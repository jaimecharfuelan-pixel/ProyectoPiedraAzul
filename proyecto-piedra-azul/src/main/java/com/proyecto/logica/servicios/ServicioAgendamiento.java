package com.proyecto.logica.servicios;

import com.proyecto.logica.interfaces.IServicioAgendamiento;
import com.proyecto.logica.modelos.*;
import com.proyecto.persistencia.interfaces.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioAgendamiento implements IServicioAgendamiento {

    private final IRepositorioCitas repoCitas;
    private final IRepositorioJornadaLaboral repoJornada;

    public ServicioAgendamiento(IRepositorioCitas prmRepoCitas, IRepositorioJornadaLaboral prmRepoJornada) {
        this.repoCitas = prmRepoCitas;
        this.repoJornada = prmRepoJornada;
    }

    @Override
    public List<LocalTime> consultarDisponibilidad(int prmIdMedico, LocalDate prmFecha) {
        List<LocalTime> horariosLibres = new ArrayList<>();
        
        // 1. Obtener la jornada del médico para ese día de la semana
        String diaSemana = traducirDia(prmFecha.getDayOfWeek());
        List<JornadaLaboral> jornadas = repoJornada.listar(); 
        
        JornadaLaboral jornadaHoy = jornadas.stream()
            .filter(j -> j.getIdUsuario() == prmIdMedico && j.getDiaSemana().equalsIgnoreCase(diaSemana))
            .findFirst()
            .orElse(null);

        if (jornadaHoy == null) return horariosLibres; // No trabaja ese día

        // 2. Obtener citas ya ocupadas ese día
        List<Cita> citasOcupadas = repoCitas.listar().stream()
            .filter(c -> c.getIdMedico() == prmIdMedico && c.getFecha().equals(prmFecha))
            .toList();

        // 3. Generar franjas (Ejemplo: cada 30 minutos)
        LocalTime actual = jornadaHoy.getHoraInicio();
        while (actual.isBefore(jornadaHoy.getHoraFin())) {
            LocalTime horaEvaluar = actual;
            
            // Verificar si esta hora choca con alguna cita existente
            boolean estaOcupado = citasOcupadas.stream().anyMatch(c -> 
                (horaEvaluar.equals(c.getHoraInicio()) || 
                (horaEvaluar.isAfter(c.getHoraInicio()) && horaEvaluar.isBefore(c.getHoraFin())))
            );

            if (!estaOcupado) {
                horariosLibres.add(horaEvaluar);
            }
            
            actual = actual.plusMinutes(30); // Aquí podrías usar la duración del médico
        }

        return horariosLibres;
    }

    @Override
    public boolean agendarCitaWeb(int prmIdPaciente, int prmIdMedico, LocalDate prmFecha, LocalTime prmHora) {
        // Validar primero si sigue disponible
        List<LocalTime> disponibles = consultarDisponibilidad(prmIdMedico, prmFecha);
        
        if (disponibles.contains(prmHora)) {
            Cita nuevaCita = new Cita();
            nuevaCita.setIdPaciente(prmIdPaciente);
            nuevaCita.setIdMedico(prmIdMedico);
            nuevaCita.setFecha(prmFecha);
            nuevaCita.setHoraInicio(prmHora);
            nuevaCita.setHoraFin(prmHora.plusMinutes(30));
            nuevaCita.setIdEstadoCita(2); // Estado 'Pendiente' o 'Activa'
            
            return repoCitas.guardar(nuevaCita) > 0;
        }
        return false;
    }

    private String traducirDia(DayOfWeek prmDay) {
        return switch (prmDay) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    @Override
    public List<Cita> listarCitasPorMedico(int prmIdMedico, LocalDate prmFecha) {
        return repoCitas.listar().stream()
                .filter(c -> c.getIdMedico() == prmIdMedico && c.getFecha().equals(prmFecha))
                .toList();
    }

    @Override
    public boolean crearCitaManual(Cita prmCita) {
        return repoCitas.guardar(prmCita) > 0;
    }
}