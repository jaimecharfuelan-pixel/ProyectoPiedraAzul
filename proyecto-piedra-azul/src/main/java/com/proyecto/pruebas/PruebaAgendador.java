package com.proyecto.pruebas;

import com.proyecto.persistencia.repositorios.RepositorioAgendadorPersonaImpl;
import com.proyecto.persistencia.interfaces.IRepositorioAgendadorPersona;
import com.proyecto.logica.modelos.Agendador;

import java.util.List;

public class PruebaAgendador {

    public static void main(String[] args) {

        System.out.println("===== PRUEBA AGENDADOR =====");

        IRepositorioAgendadorPersona repo = new RepositorioAgendadorPersonaImpl();

        // Persona previamente creadaa
        int idPersona = 5;

        System.out.println("\n--- GUARDAR AGENDADOR ---");

        boolean guardado = repo.guardar(idPersona);

        System.out.println("Agendador guardado: " + guardado);

        System.out.println("\n--- LISTAR AGENDADORES ---");

        List<Agendador> lista = repo.listar();

        for (Agendador a : lista) {
            System.out.println("ID Persona: " + a.getId());
        }

        System.out.println("\n--- BUSCAR AGENDADOR ---");

        Agendador ag = repo.buscar(idPersona);

        if (ag != null) {
            System.out.println("Agendador encontrado con ID: " + ag.getId());
        } else {
            System.out.println("Agendador no encontrado");
        }

        System.out.println("\n--- LISTA FINAL ---");

        lista = repo.listar();

        for (Agendador a : lista) {
            System.out.println("ID Persona: " + a.getId());
        }

        System.out.println("\n===== FIN PRUEBA =====");

    }
}