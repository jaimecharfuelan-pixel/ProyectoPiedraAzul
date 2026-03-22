
/*Maneja la lógica de buscar citas, validar disponibilidad y crear citas (tanto por agendador como por paciente). 


Yo como agendador de citas necesito listar las citas médicas de un determinado médico/terapista en una fecha 
determinada para visualizar el listado y la cantidad de citas. Contexto: Se sugiere diseñar un sistema de búsqueda 
con resultados en una tabla.
Yo como agendador de citas necesito crear una nueva cita de un paciente que me ha contactado por WhatsApp para hacer 
efectiva esa cita. Contexto: los datos que se deben capturar del paciente son: Número de documento de identidad, nombres 
y apellidos, celular, género (Hombre, Mujer, Otro), fecha de nacimiento (opcional) y correo electrónico (opcional); los datos 
de la cita son: Médico/terapista, hora. Tener en cuenta el intervalo de tiempo de cada médico/terapista.
*/




package com.proyecto.logica.interfaces;


import com.proyecto.logica.modelos.Cita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface IServicioAgendamiento {
    // F1 y F2: Para el agendador
    List<Cita> listarCitasPorMedico(int prmIdMedico, LocalDate prmFecha);
    boolean crearCitaManual(Cita prmCita);

    // F3: La función de disponibilidad que pediste
    List<LocalTime> consultarDisponibilidad(int prmIdMedico, LocalDate prmFecha);
    
    // F3: Agendamiento desde la Web
    boolean agendarCitaWeb(int prmIdPaciente, int prmIdMedico, LocalDate prmFecha, LocalTime prmHora);
}