package com.proyecto.microservicio_agendamiento.servicio.template;

import com.proyecto.microservicio_agendamiento.mensajeria.PublicadorCitas;
import com.proyecto.microservicio_agendamiento.modelo.Cita;
import com.proyecto.microservicio_agendamiento.repositorio.RepositorioCitas;

/**
 * CLASE ABSTRACTA - Patrón Template Method
 *
 * Define el esqueleto del algoritmo para procesar una cita médica.
 * El orden de los pasos es fijo (final), pero cada subclase implementa
 * los pasos variables según el tipo de flujo (Web o Manual).
 *
 * Analogía con el ejemplo de tarjetas de crédito (ej1):
 *   CreditCard.isValid()          →  CitaProcesoTemplate.procesarCita()
 *   isNumOfDigitsValid()          →  validarDatosEspecificos()
 *   isValidPrefix()               →  asignarEstadoInicial()
 *   isAccountInGoodStand()        →  despuesDeGuardar()
 *   isExpDtValid() / hasValidChars() →  validarCitaNoNula() / validarCamposObligatorios()
 */
public abstract class CitaProcesoTemplate {

    /**
     * TEMPLATE METHOD — no puede ser sobreescrito por las subclases.
     * Define el algoritmo completo de procesamiento de una cita.
     */
    public final boolean procesarCita(Cita cita,
                                      RepositorioCitas repoCitas,
                                      PublicadorCitas publicador) {

        // Paso 1: Validar que la cita no sea nula (común para todos)
        if (!validarCitaNoNula(cita)) {
            System.out.println("❌ Error: La cita es nula");
            return false;
        }

        // Paso 2: Validar campos obligatorios (común para todos)
        if (!validarCamposObligatorios(cita)) {
            System.out.println("❌ Error: Faltan campos obligatorios en la cita");
            return false;
        }

        // Paso 3: Validaciones específicas por tipo de flujo (ABSTRACTO - cada subclase lo define)
        if (!validarDatosEspecificos(cita)) {
            System.out.println("❌ Error: Validación específica del flujo falló");
            return false;
        }

        // Paso 4: Asignar estado inicial según el tipo de flujo (ABSTRACTO - cada subclase lo define)
        asignarEstadoInicial(cita);

        // Paso 5: Persistir y publicar evento (común para todos)
        Cita guardada = repoCitas.save(cita);
        publicador.publicarCitaCreada(guardada);

        // Paso 6: Hook — acciones post-guardado específicas por flujo
        despuesDeGuardar(guardada);

        return true;
    }

    /**
     * PASO 1 — Validar que la cita no sea nula (común, no sobreescribible en subclases).
     */
    private boolean validarCitaNoNula(Cita cita) {
        return cita != null;
    }

    /**
     * PASO 2 — Validar campos obligatorios comunes a todo tipo de cita.
     * Verifica que paciente, médico, fecha y hora estén presentes.
     */
    private boolean validarCamposObligatorios(Cita cita) {
        return cita.getIdPaciente() > 0
                && cita.getIdMedico() > 0
                && cita.getFecha() != null
                && cita.getHoraInicio() != null
                && cita.getHoraFin() != null;
    }

    /**
     * PASO 3 — ABSTRACTO: Validaciones específicas por tipo de flujo.
     * Flujo Web: verifica disponibilidad en la jornada del médico.
     * Flujo Manual: verifica que la cita tenga motivo o datos adicionales.
     */
    protected abstract boolean validarDatosEspecificos(Cita cita);

    /**
     * PASO 4 — ABSTRACTO: Asignar el estado inicial de la cita según el flujo.
     * Flujo Web:    estado PENDIENTE (requiere confirmación).
     * Flujo Manual: estado CONFIRMADA (el agendador ya la confirmó).
     */
    protected abstract void asignarEstadoInicial(Cita cita);

    /**
     * PASO 6 — Hook: Acciones opcionales después de guardar.
     * Las subclases pueden sobreescribir este método para agregar
     * comportamiento adicional sin alterar el algoritmo principal.
     */
    protected void despuesDeGuardar(Cita cita) {
        // Implementación por defecto vacía — las subclases la extienden si necesitan
    }
}
