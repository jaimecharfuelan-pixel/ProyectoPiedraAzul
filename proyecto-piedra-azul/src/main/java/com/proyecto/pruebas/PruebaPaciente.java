package com.proyecto.pruebas;

import com.proyecto.persistencia.interfaces.IRepositorioPaciente;
import com.proyecto.persistencia.repositorios.RepositorioPacienteImpl;
import com.proyecto.logica.modelos.Paciente;

import java.util.List;

public class PruebaPaciente {

    public static void main(String[] args) {

        IRepositorioPaciente repo = new RepositorioPacienteImpl();

        System.out.println("===== PRUEBA PACIENTE =====");

        // 1️⃣ GUARDAR PACIENTE
        System.out.println("\n--- GUARDAR PACIENTE ---");

        boolean guardado1 = repo.guardar(1);
        boolean guardado2 = repo.guardar(4);

        System.out.println("Paciente 1 guardado: " + guardado1);
        System.out.println("Paciente 4 guardado: " + guardado2);

        // 2️⃣ LISTAR PACIENTES
        System.out.println("\n--- LISTAR PACIENTES ---");

        List<Paciente> lista = repo.listar();

        for (Paciente p : lista) {
            System.out.println("Paciente ID Persona: " + p.getId());
        }

        // 3️⃣ BUSCAR PACIENTE
        System.out.println("\n--- BUSCAR PACIENTE ID 1 ---");

        Paciente paciente = repo.buscar(1);

        if (paciente != null) {
            System.out.println("Paciente encontrado con ID: " + paciente.getId());
        } else {
            System.out.println("Paciente no encontrado");
        }

        // 4️⃣ ELIMINAR PACIENTE (opcional)
        System.out.println("\n--- ELIMINAR PACIENTE ID 4 ---");

        boolean eliminado = repo.eliminar(4);

        System.out.println("Paciente eliminado: " + eliminado);

        // 5️⃣ LISTA FINAL
        System.out.println("\n--- LISTA FINAL ---");

        lista = repo.listar();

        for (Paciente p : lista) {
            System.out.println("Paciente ID Persona: " + p.getId());
        }

        System.out.println("\n===== FIN PRUEBA =====");
    }
}