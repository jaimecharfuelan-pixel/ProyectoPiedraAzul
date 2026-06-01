package com.proyecto.presentacion.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilidad centralizada para gestionar estados de citas.
 * 
 * Proporciona mapeo entre ID de estado, colores CSS, iconos Unicode, 
 * nombres y descripciones. Facilita la visualización consistente 
 * de estados en toda la aplicación.
 * 
 * Estados soportados:
 * - 1: Cancelada (Rojo)
 * - 2: Pendiente (Amarillo)
 * - 3: Confirmada (Verde)
 * - 4: Completada (Azul)
 * - 5: No Asistió (Gris)
 */
public final class EstadoCita {

    // ───────────────────────────────────────────────────────────
    // CONSTANTES DE IDs DE ESTADO (coinciden con BD)
    // ───────────────────────────────────────────────────────────
    public static final int ID_CANCELADA    = 1;
    public static final int ID_PENDIENTE    = 2;
    public static final int ID_CONFIRMADA   = 3;
    public static final int ID_COMPLETADA   = 4;
    public static final int ID_NO_ASISTIO   = 5;

    // ───────────────────────────────────────────────────────────
    // DATOS DE CADA ESTADO
    // ───────────────────────────────────────────────────────────
    private static final Map<Integer, DatosEstado> MAPEO_ESTADOS = new HashMap<>();

    static {
        // Cancelada (Rojo)
        MAPEO_ESTADOS.put(ID_CANCELADA, new DatosEstado(
                "Cancelada",
                "La cita ha sido cancelada",
                "#dc2626",           // Rojo
                "#fecaca",           // Fondo rojo claro
                "✕"                  // Ícono Unicode
        ));

        // Pendiente (Amarillo/Naranja)
        MAPEO_ESTADOS.put(ID_PENDIENTE, new DatosEstado(
                "Pendiente",
                "A la espera de confirmación",
                "#ea8c00",           // Naranja
                "#fed7aa",           // Fondo naranja claro
                "⏳"                  // Ícono Unicode
        ));

        // Confirmada (Verde)
        MAPEO_ESTADOS.put(ID_CONFIRMADA, new DatosEstado(
                "Confirmada",
                "La cita está confirmada",
                "#16a34a",           // Verde
                "#bbf7d0",           // Fondo verde claro
                "✓"                  // Ícono Unicode
        ));

        // Completada (Azul oscuro)
        MAPEO_ESTADOS.put(ID_COMPLETADA, new DatosEstado(
                "Completada",
                "La cita fue realizada",
                "#003E72",           // Azul principal del proyecto
                "#e0f0ff",           // Fondo azul muy claro
                "✓✓"                 // Ícono Unicode (doble check)
        ));

        // No Asistió (Gris)
        MAPEO_ESTADOS.put(ID_NO_ASISTIO, new DatosEstado(
                "No Asistió",
                "El paciente no asistió",
                "#6b7280",           // Gris
                "#f3f4f6",           // Fondo gris claro
                "✗"                  // Ícono Unicode
        ));
    }

    private EstadoCita() {} // Clase utilitaria, no instanciable

    // ───────────────────────────────────────────────────────────
    // API PÚBLICA
    // ───────────────────────────────────────────────────────────

    /**
     * Obtiene el nombre amigable del estado (ej: "Confirmada")
     */
    public static String getNombre(Integer idEstado) {
        if (idEstado == null) return "Desconocido";
        DatosEstado datos = MAPEO_ESTADOS.get(idEstado);
        return datos != null ? datos.nombre : "Desconocido";
    }

    /**
     * Obtiene la descripción del estado (ej: "La cita está confirmada")
     */
    public static String getDescripcion(Integer idEstado) {
        if (idEstado == null) return "Estado desconocido";
        DatosEstado datos = MAPEO_ESTADOS.get(idEstado);
        return datos != null ? datos.descripcion : "Estado desconocido";
    }

    /**
     * Obtiene el color principal (hexadecimal) del estado
     * Para usar en fondos, bordes, etc.
     */
    public static String getColor(Integer idEstado) {
        if (idEstado == null) return "#9ca3af";
        DatosEstado datos = MAPEO_ESTADOS.get(idEstado);
        return datos != null ? datos.colorPrincipal : "#9ca3af";
    }

    /**
     * Obtiene el color de fondo claro del estado
     * Para usar como fondo de etiquetas/badges
     */
    public static String getColorFondo(Integer idEstado) {
        if (idEstado == null) return "#f0f0f0";
        DatosEstado datos = MAPEO_ESTADOS.get(idEstado);
        return datos != null ? datos.colorFondo : "#f0f0f0";
    }

    /**
     * Obtiene el ícono Unicode del estado (ej: "✓")
     */
    public static String getIcono(Integer idEstado) {
        if (idEstado == null) return "?";
        DatosEstado datos = MAPEO_ESTADOS.get(idEstado);
        return datos != null ? datos.icono : "?";
    }

    /**
     * Genera CSS inline para aplicar a un control (Label, Button, etc)
     * con el estilo del estado completo.
     * 
     * Ejemplo de uso:
     *   Label lbl = new Label();
     *   lbl.setStyle(EstadoCita.generarEstiloCSS(citaDTO.getIdEstadoCita()));
     */
    public static String generarEstiloCSS(Integer idEstado) {
        if (idEstado == null) {
            return "-fx-background-color: #f0f0f0; -fx-text-fill: #666666; -fx-padding: 8px 12px; -fx-background-radius: 6;";
        }
        DatosEstado datos = MAPEO_ESTADOS.get(idEstado);
        if (datos == null) {
            return "-fx-background-color: #f0f0f0; -fx-text-fill: #666666; -fx-padding: 8px 12px; -fx-background-radius: 6;";
        }
        return String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-padding: 8px 12px; -fx-background-radius: 6; -fx-font-weight: bold;",
                datos.colorFondo,
                datos.colorPrincipal
        );
    }

    /**
     * Retorna el CSS para clase de estilo (para .css files)
     * Incluye nombre de clase, color fondo, color texto
     */
    public static String generarClaseCSS(Integer idEstado) {
        if (idEstado == null) {
            return ".estado-desconocido { -fx-background-color: #f0f0f0; -fx-text-fill: #666666; }";
        }
        DatosEstado datos = MAPEO_ESTADOS.get(idEstado);
        if (datos == null) {
            return ".estado-desconocido { -fx-background-color: #f0f0f0; -fx-text-fill: #666666; }";
        }
        String nombreClase = ".estado-" + datos.nombre.toLowerCase().replace(" ", "-");
        return String.format(
                "%s { -fx-background-color: %s; -fx-text-fill: %s; -fx-padding: 8px 12px; -fx-background-radius: 6; -fx-font-weight: bold; }",
                nombreClase,
                datos.colorFondo,
                datos.colorPrincipal
        );
    }

    /**
     * Valida si el ID de estado es válido
     */
    public static boolean esEstadoValido(Integer idEstado) {
        return idEstado != null && MAPEO_ESTADOS.containsKey(idEstado);
    }

    /**
     * Obtiene todos los IDs de estado válidos
     */
    public static int[] obtenerTodosLosIds() {
        return MAPEO_ESTADOS.keySet().stream().mapToInt(Integer::intValue).toArray();
    }

    // ───────────────────────────────────────────────────────────
    // CLASE INTERNA PRIVADA: Encapsula datos de un estado
    // ───────────────────────────────────────────────────────────
    private static class DatosEstado {
        final String nombre;
        final String descripcion;
        final String colorPrincipal;
        final String colorFondo;
        final String icono;

        DatosEstado(String nombre, String descripcion, String colorPrincipal, String colorFondo, String icono) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.colorPrincipal = colorPrincipal;
            this.colorFondo = colorFondo;
            this.icono = icono;
        }
    }
}
