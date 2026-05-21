package com.proyecto.microservicio_agendamiento.estado;

/**
 * TABLA DE TRANSICIONES DEL PATRÓN STATE
 * 
 * Define qué transiciones de estado son VÁLIDAS para una consulta.
 * 
 * Formato de la tabla:
 * ┌─────────────────────────────────────────────────────────────────┐
 * │ ESTADO ACTUAL   │ CONFIRMAR │ CANCELAR │ COMPLETAR │ NO ASISTIO │
 * ├─────────────────────────────────────────────────────────────────┤
 * │ PENDIENTE       │    ✓      │    ✓     │     ✗     │     ✗      │
 * │ CONFIRMADA      │    ✓      │    ✓     │     ✓     │     ✓      │
 * │ COMPLETADA      │    ✗      │    ✗     │     ✓     │     ✗      │
 * │ CANCELADA       │    ✗      │    ✓     │     ✗     │     ✗      │
 * │ NO ASISTIO      │    ✗      │    ✗     │     ✗     │     ✓      │
 * └─────────────────────────────────────────────────────────────────┘
 * 
 * ✓ = Transición válida
 * ✗ = Transición INVÁLIDA (se rechaza la operación)
 */
public class TablaTransiciones {
    
    /**
     * Imprime la tabla de transiciones en consola para referencia visual.
     */
    public static void mostrarTabla() {
        System.out.println("\n═══════════════════════════════════════════════════════════════════");
        System.out.println("TABLA DE TRANSICIONES - PATRÓN STATE (CONSULTAS)");
        System.out.println("═══════════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("┌──────────────────┬────────────┬──────────┬───────────┬─────────────┐");
        System.out.println("│ ESTADO ACTUAL    │ CONFIRMAR  │ CANCELAR │ COMPLETAR │ NO ASISTIO  │");
        System.out.println("├──────────────────┼────────────┼──────────┼───────────┼─────────────┤");
        System.out.println("│ Pendiente        │     ✓      │    ✓     │     ✗     │      ✗      │");
        System.out.println("│ Confirmada       │     ✓      │    ✓     │     ✓     │      ✓      │");
        System.out.println("│ Completada       │     ✗      │    ✗     │     ✓     │      ✗      │");
        System.out.println("│ Cancelada        │     ✗      │    ✓     │     ✗     │      ✗      │");
        System.out.println("│ No Asistió       │     ✗      │    ✗     │     ✗     │      ✓      │");
        System.out.println("└──────────────────┴────────────┴──────────┴───────────┴─────────────┘");
        System.out.println();
        System.out.println("LEYENDA:");
        System.out.println("  ✓ = Transición VÁLIDA (se ejecuta)");
        System.out.println("  ✗ = Transición INVÁLIDA (se rechaza)");
        System.out.println();
        System.out.println("ESTADOS TERMINALES (NO cambian):");
        System.out.println("  • Completada   → La consulta fue realizada");
        System.out.println("  • Cancelada    → La consulta fue cancelada");
        System.out.println("  • No Asistió   → El paciente no se presentó");
        System.out.println();
        System.out.println("ESTADOS TRANSITORIOS (pueden cambiar):");
        System.out.println("  • Pendiente    → Espera confirmación");
        System.out.println("  • Confirmada   → Listo para realizarse");
        System.out.println("═══════════════════════════════════════════════════════════════════\n");
    }
    
    /**
     * Documenta los flujos principales de una consulta.
     */
    public static void mostrarFlujosPrincipales() {
        System.out.println("\n═══════════════════════════════════════════════════════════════════");
        System.out.println("FLUJOS PRINCIPALES");
        System.out.println("═══════════════════════════════════════════════════════════════════\n");
        
        System.out.println("FLUJO NORMAL (Éxito):");
        System.out.println("  Pendiente → Confirmada → Completada");
        System.out.println("  (Paciente confirma, asiste y consulta se realiza)");
        System.out.println();
        
        System.out.println("FLUJO CON INASISTENCIA:");
        System.out.println("  Pendiente → Confirmada → No Asistió");
        System.out.println("  (Paciente confirma pero no asiste)");
        System.out.println();
        
        System.out.println("FLUJO CON CANCELACIÓN ANTES DE CONFIRMAR:");
        System.out.println("  Pendiente → Cancelada");
        System.out.println("  (Paciente rechaza la cita)");
        System.out.println();
        
        System.out.println("FLUJO CON CANCELACIÓN DESPUÉS DE CONFIRMAR:");
        System.out.println("  Pendiente → Confirmada → Cancelada");
        System.out.println("  (Paciente confirma pero luego cancela)");
        System.out.println();
        
        System.out.println("═══════════════════════════════════════════════════════════════════\n");
    }
}
