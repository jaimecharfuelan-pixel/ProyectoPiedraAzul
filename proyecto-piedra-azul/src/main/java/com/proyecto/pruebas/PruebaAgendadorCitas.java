package com.proyecto.pruebas;

import com.proyecto.logica.modelos.Cita;
import com.proyecto.logica.modelos.Paciente;
import com.proyecto.logica.modelos.Medico;
import com.proyecto.logica.servicios.ServicioAgendador;

import java.time.LocalDateTime;
import java.util.List;

public class PruebaAgendadorCitas {

    public static void main(String[] args) {

        System.out.println("===== PRUEBA AGENDADOR =====");

        ServicioAgendador servicio = new ServicioAgendador();

        // paciente existente
        Paciente paciente = new Paciente(
                1, "", "", "", "", "",
                null, "");

        // medico existente
        Medico medico = new Medico(
                4, "", "", "", "", "",
                null, "", "");

        // crear cita
        Cita cita = new Cita(
                0,
                paciente,
                medico,
                LocalDateTime.of(2026, 3, 20, 10, 0),
                "AGENDADA");

        boolean creada = servicio.agendarCita(cita);

        System.out.println("Cita creada: " + creada);

        System.out.println("\n--- LISTAR CITAS ---");

        List<Cita> citas = servicio.listarCitas();

        for (Cita c : citas) {

            System.out.println(
                    "Cita ID: " + c.getId() +
                            " Paciente: " + c.getPaciente().getId() +
                            " Medico: " + c.getMedico().getId() +
                            " Fecha: " + c.getFechaHora());
        }

        System.out.println("===== FIN PRUEBA =====");
    }
}