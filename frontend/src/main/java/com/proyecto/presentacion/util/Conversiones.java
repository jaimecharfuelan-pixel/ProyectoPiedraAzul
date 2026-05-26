package com.proyecto.presentacion.util;

import java.time.DayOfWeek;

/**
 * Utilidades de conversión compartidas entre controladores.
 * Elimina la duplicación de traducirDia() y generoAId() en múltiples controladores.
 */
public final class Conversiones {

    private Conversiones() {}

    /** Convierte el nombre en inglés de DayOfWeek al español usado en la BD. */
    public static String traducirDia(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY    -> "Lunes";
            case TUESDAY   -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY  -> "Jueves";
            case FRIDAY    -> "Viernes";
            case SATURDAY  -> "Sábado";
            case SUNDAY    -> "Domingo";
        };
    }

    /** Sobrecarga para recibir el nombre en String (como viene de DayOfWeek.name()). */
    public static String traducirDia(String dayOfWeekEn) {
        return switch (dayOfWeekEn) {
            case "MONDAY"    -> "Lunes";
            case "TUESDAY"   -> "Martes";
            case "WEDNESDAY" -> "Miércoles";
            case "THURSDAY"  -> "Jueves";
            case "FRIDAY"    -> "Viernes";
            case "SATURDAY"  -> "Sábado";
            case "SUNDAY"    -> "Domingo";
            default          -> dayOfWeekEn;
        };
    }

    /** Convierte el nombre de género en español a su ID de BD. */
    public static int generoAId(String genero) {
        if (genero == null) return 4;
        return switch (genero) {
            case "Masculino"  -> 1;
            case "Femenino"   -> 2;
            case "No Binario" -> 3;
            default           -> 4;
        };
    }

    /** Convierte el ID de género a su nombre en español. */
    public static String idAGenero(Integer idGenero) {
        if (idGenero == null) return "Prefiero no decir";
        return switch (idGenero) {
            case 1  -> "Masculino";
            case 2  -> "Femenino";
            case 3  -> "No Binario";
            default -> "Prefiero no decir";
        };
    }
}
