package com.proyecto.pruebas;

import com.proyecto.persistencia.repositorios.RepositorioPersonaImpl;
import com.proyecto.logica.modelos.Persona;

import java.time.LocalDate;

public class PruebaPersona {

    public static void main(String[] args) {

        System.out.println("===== PRUEBA PERSONA =====");

        RepositorioPersonaImpl repo = new RepositorioPersonaImpl();

        Persona p1 = new Persona(
                0,
                "123456",
                "Juan",
                "Perez",
                "3001111111",
                "M",
                LocalDate.of(1990, 5, 10),
                "juan@email.com") {
        };

        Persona p2 = new Persona(
                0,
                "789456",
                "Maria",
                "Lopez",
                "3002222222",
                "F",
                LocalDate.of(1995, 8, 20),
                "maria@email.com") {
        };

        Persona p3 = new Persona(
                0,
                "456123",
                "Carlos",
                "Gomez",
                "3003333333",
                "M",
                LocalDate.of(1985, 3, 15),
                "carlos@email.com") {
        };

        int id1 = repo.guardar(p1);
        int id2 = repo.guardar(p2);
        int id3 = repo.guardar(p3);

        System.out.println("Persona creada con ID: " + id1);
        System.out.println("Persona creada con ID: " + id2);
        System.out.println("Persona creada con ID: " + id3);

        System.out.println("===== FIN PRUEBA =====");

    }
}