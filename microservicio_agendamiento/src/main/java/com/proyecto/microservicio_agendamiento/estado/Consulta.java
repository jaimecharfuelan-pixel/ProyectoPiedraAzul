package com.proyecto.microservicio_agendamiento.estado;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * CLASE CONSULTA - Implementa el patrón STATE.
 * 
 * Una consulta es una cita médica que tiene:
 * - Datos de identidad (paciente, médico, fecha, hora)
 * - Un ESTADO actual que cambia durante su ciclo de vida
 * 
 * El patrón STATE encapsula todo el comportamiento que depende del estado
 * en clases concretas (EstadoPendiente, EstadoConfirmada, etc).
 * 
 * VENTAJAS del patrón STATE:
 * 1. Cada estado maneja su propio comportamiento
 * 2. Las transiciones válidas se definen EN el estado
 * 3. Es fácil agregar nuevos estados sin cambiar esta clase
 * 4. El código es más limpio: no hay cascadas de if/else
 */
public class Consulta {
    
    // Identidad de la consulta (datos persistentes)
    private int idConsulta;
    private int idPaciente;
    private int idMedico;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    
    // EL ESTADO ACTUAL - Es la clave del patrón STATE
    // Este objeto es lo que VARÍA durante la vida de la consulta
    private EstadoConsultaStrategy estadoActual;
    
    // Constructor para crear una nueva consulta
    public Consulta(int idPaciente, int idMedico, LocalDate fecha, 
                   LocalTime horaInicio, LocalTime horaFin) {
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        // Una consulta SIEMPRE comienza en estado PENDIENTE
        this.estadoActual = FabricaEstados.crearEstadoInicial();
    }
    
    // Constructor para cargar una consulta existente desde BD
    public Consulta(int idConsulta, int idPaciente, int idMedico, 
                   LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, 
                   int idEstado) {
        this.idConsulta = idConsulta;
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estadoActual = FabricaEstados.crearEstado(idEstado);
    }
    
    // ═════════════════════════════════════════════════════════════════
    // MÉTODOS QUE DELEGAN AL PATRÓN STATE
    // Aquí es donde ocurre la magia del patrón:
    // La clase Consulta NO decide si una transición es válida,
    // sino que le pregunta al estado actual
    // ═════════════════════════════════════════════════════════════════
    
    /**
     * Intenta confirmar esta consulta.
     * La validación se delega al estado actual.
     * 
     * @return true si la confirmación fue exitosa, false si fue rechazada
     */
    public boolean confirmar() {
        boolean resultado = estadoActual.confirmar();
        if (resultado) {
            // Si fue válido, cambiar de estado
            transicionarA(3); // ID 3 = Confirmada
        }
        return resultado;
    }
    
    /**
     * Intenta cancelar esta consulta.
     * La validación se delega al estado actual.
     * 
     * @return true si la cancelación fue exitosa, false si fue rechazada
     */
    public boolean cancelar() {
        boolean resultado = estadoActual.cancelar();
        if (resultado && !estadoActual.obtenerNombre().equals("Cancelada")) {
            // Si fue válido y no está ya cancelada, cambiar de estado
            transicionarA(1); // ID 1 = Cancelada
        }
        return resultado;
    }
    
    /**
     * Intenta completar esta consulta (marcarla como realizada).
     * La validación se delega al estado actual.
     * 
     * @return true si la finalización fue exitosa, false si fue rechazada
     */
    public boolean completar() {
        boolean resultado = estadoActual.completar();
        if (resultado && !estadoActual.obtenerNombre().equals("Completada")) {
            // Si fue válido y no está ya completada, cambiar de estado
            transicionarA(4); // ID 4 = Completada
        }
        return resultado;
    }
    
    /**
     * Intenta marcar esta consulta como "No Asistió".
     * La validación se delega al estado actual.
     * 
     * @return true si fue exitosa, false si fue rechazada
     */
    public boolean marcarNoAsistio() {
        boolean resultado = estadoActual.marcarNoAsistio();
        if (resultado && !estadoActual.obtenerNombre().equals("No Asistió")) {
            // Si fue válido y no está ya en este estado, cambiar de estado
            transicionarA(5); // ID 5 = No Asistió
        }
        return resultado;
    }
    
    // ═════════════════════════════════════════════════════════════════
    // GESTIÓN INTERNA DEL ESTADO
    // ═════════════════════════════════════════════════════════════════
    
    /**
     * Realiza la transición a un nuevo estado.
     * 
     * @param idNuevoEstado ID del estado destino
     */
    private void transicionarA(int idNuevoEstado) {
        EstadoConsultaStrategy estadoAnterior = estadoActual;
        estadoActual = FabricaEstados.crearEstado(idNuevoEstado);
        System.out.println("[TRANSICIÓN] " + estadoAnterior.obtenerNombre() + 
                          " → " + estadoActual.obtenerNombre());
    }
    
    /**
     * Establece el estado directamente (para cargar desde BD).
     * 
     * @param idEstado ID del estado
     */
    public void establecerEstado(int idEstado) {
        this.estadoActual = FabricaEstados.crearEstado(idEstado);
    }
    
    // ═════════════════════════════════════════════════════════════════
    // GETTERS
    // ═════════════════════════════════════════════════════════════════
    
    public int getIdConsulta() { return idConsulta; }
    public int getIdPaciente() { return idPaciente; }
    public int getIdMedico() { return idMedico; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    
    // Getters del estado actual
    public String getEstadoNombre() {
        return estadoActual.obtenerNombre();
    }
    
    public int getEstadoID() {
        return estadoActual.obtenerID();
    }
    
    public String getEstadoDescripcion() {
        return estadoActual.obtenerDescripcion();
    }
    
    // ═════════════════════════════════════════════════════════════════
    // SETTERS (para datos básicos)
    // ═════════════════════════════════════════════════════════════════
    
    public void setIdConsulta(int idConsulta) { this.idConsulta = idConsulta; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    
    // ═════════════════════════════════════════════════════════════════
    // INFORMACIÓN
    // ═════════════════════════════════════════════════════════════════
    
    @Override
    public String toString() {
        return String.format(
            "Consulta [ID: %d | Paciente: %d | Médico: %d | %s %s-%s | Estado: %s]",
            idConsulta, idPaciente, idMedico, fecha, horaInicio, horaFin, 
            estadoActual.obtenerNombre()
        );
    }
    
    /**
     * Imprime información detallada de la consulta.
     */
    public void mostrarDetalle() {
        System.out.println("\n┌─────────────────────────────────────────────┐");
        System.out.println("│ DETALLE DE CONSULTA                         │");
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.printf("│ ID Consulta:    %-30d │%n", idConsulta);
        System.out.printf("│ Paciente:       %-30d │%n", idPaciente);
        System.out.printf("│ Médico:         %-30d │%n", idMedico);
        System.out.printf("│ Fecha:          %-30s │%n", fecha);
        System.out.printf("│ Hora Inicio:    %-30s │%n", horaInicio);
        System.out.printf("│ Hora Fin:       %-30s │%n", horaFin);
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.printf("│ Estado:         %-30s │%n", estadoActual.obtenerNombre());
        System.out.printf("│ Descripción:    %-30s │%n", estadoActual.obtenerDescripcion());
        System.out.println("└─────────────────────────────────────────────┘\n");
    }
}
