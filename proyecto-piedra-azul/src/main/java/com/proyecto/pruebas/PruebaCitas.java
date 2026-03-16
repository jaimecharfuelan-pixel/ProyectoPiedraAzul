package com.proyecto.pruebas;

import com.proyecto.persistencia.repositorios.RepositorioCitasImpl;
import com.proyecto.persistencia.interfaces.IRepositorioCitas;
import com.proyecto.logica.modelos.*;

import java.time.LocalDateTime;
import java.util.List;

public class PruebaCitas {

    public static void main(String[] args) {

        IRepositorioCitas repo = new RepositorioCitasImpl();

        Paciente paciente = new Paciente(1, "", "", "", "", "", null, "");
        Medico medico = new Medico(4, "", "", "", "", "", null, "", "");

        Cita cita = new Cita(
                0,
                paciente,
                medico,
                LocalDateTime.now(),
                "AGENDADA");

        System.out.println("===== PRUEBA CITAS =====");

        boolean guardado = repo.guardar(cita);

        System.out.println("Cita guardada: " + guardado);

        System.out.println("\n--- LISTAR CITAS ---");

        List<Cita> lista = repo.listar();

        for (Cita c : lista) {

            System.out.println(
                    c.getId() + " | Paciente: "
                            + c.getPaciente().getId()
                            + " | Medico: "
                            + c.getMedico().getId()
                            + " | Estado: "
                            + c.getEstado());
        }

        System.out.println("===== FIN PRUEBA =====");

    }
}