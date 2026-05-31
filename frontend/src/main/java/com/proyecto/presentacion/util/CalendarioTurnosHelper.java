package com.proyecto.presentacion.util;

import com.proyecto.presentacion.dto.MedicoDTO;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Utilidades compartidas para calendarios y combos de hora basados en jornadas laborales.
 */
public final class CalendarioTurnosHelper {

    private static final String ESTILO_SIN_TURNO = "-fx-background-color: #f0f0f0; -fx-text-fill: #bbb;";
    private static final String ESTILO_CON_TURNO   = "-fx-background-color: #eff6ff; -fx-text-fill: #1e40af;";

    private CalendarioTurnosHelper() {}

    /** id_usuario del médico para consultar jornadas; usa idPersona como fallback. */
    public static int idUsuarioDeMedico(MedicoDTO medico) {
        if (medico == null) return -1;
        if (medico.getIdUsuario() != null && medico.getIdUsuario() > 0) {
            return medico.getIdUsuario();
        }
        return medico.getIdMedico();
    }

    public static void aplicarCalendarioPorTurnos(DatePicker datePicker,
                                                   List<String> diasConJornada,
                                                   boolean bloquearPasadas) {
        datePicker.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                if (empty || d == null) {
                    setStyle("");
                    return;
                }
                boolean esPasada = bloquearPasadas && d.isBefore(LocalDate.now());
                String nombreDia = Conversiones.traducirDia(d.getDayOfWeek());
                boolean sinJornada = !diasConJornada.isEmpty()
                        && diasConJornada.stream().noneMatch(j -> j.equalsIgnoreCase(nombreDia));
                setDisable(esPasada || sinJornada);
                if (sinJornada && !esPasada) {
                    setStyle(ESTILO_SIN_TURNO);
                } else if (!sinJornada && !esPasada) {
                    setStyle(ESTILO_CON_TURNO);
                } else {
                    setStyle("");
                }
            }
        });
    }

    public static void configurarComboHoras(ComboBox<LocalTime> combo) {
        StringConverter<LocalTime> conv = new StringConverter<>() {
            @Override public String toString(LocalTime t) {
                return t == null ? "" : String.format("%02d:%02d", t.getHour(), t.getMinute());
            }
            @Override public LocalTime fromString(String s) {
                if (s == null || s.isBlank()) return null;
                return LocalTime.parse(s.length() >= 5 ? s.substring(0, 5) : s);
            }
        };
        combo.setConverter(conv);
        combo.setCellFactory(lv -> celdaHora(conv));
        combo.setButtonCell(celdaHora(conv));
        combo.setVisibleRowCount(10);
    }

    private static ListCell<LocalTime> celdaHora(StringConverter<LocalTime> conv) {
        return new ListCell<>() {
            @Override protected void updateItem(LocalTime t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty || t == null ? null : conv.toString(t));
            }
        };
    }

    public static void cargarHorasEnCombo(ComboBox<LocalTime> combo, List<LocalTime> horarios) {
        combo.getItems().clear();
        if (horarios == null || horarios.isEmpty()) {
            combo.setValue(null);
            combo.setPromptText("Sin horarios disponibles");
            return;
        }
        combo.setPromptText("Seleccione una hora");
        combo.getItems().addAll(horarios);
        combo.setValue(horarios.get(0));
    }
}
