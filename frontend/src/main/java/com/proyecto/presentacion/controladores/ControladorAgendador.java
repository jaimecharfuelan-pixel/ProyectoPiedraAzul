package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.ClienteHttp;
import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.dto.CitaDTO;
import com.proyecto.presentacion.dto.JornadaDTO;
import com.proyecto.presentacion.dto.MedicoDTO;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ControladorAgendador implements Initializable {

    @FXML private DatePicker              dpFechaFiltro;
    @FXML private ComboBox<MedicoDTO>     cbDoctorFiltro;
    @FXML private TableView<CitaDTO>      tblCitas;
    @FXML private TableColumn<CitaDTO, String> colPaciente;
    @FXML private TableColumn<CitaDTO, String> colMedico;
    @FXML private TableColumn<CitaDTO, String> colFecha;
    @FXML private TableColumn<CitaDTO, String> colHora;
    @FXML private TableColumn<CitaDTO, Void>   colAcciones;
    @FXML private Label lblTotalHoy;
    @FXML private Label lblPendientesConfirmacionHoy;
    @FXML private Label medicosActivos;

    // Filtro activo — se mantiene al recargar tras cancelar/reagendar
    private Integer filtroIdMedico = null;
    private LocalDate filtroFecha  = null;

    private List<MedicoDTO> medicos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarMedicos();
        filtroFecha = LocalDate.now();
        cargarCitas(null, filtroFecha);
        actualizarContadores();
        // Calendario inicial: días con jornada de cualquier médico
        actualizarCalendarioFiltro(null);
        // Cuando cambia el médico, actualizar el calendario del filtro
        cbDoctorFiltro.valueProperty().addListener((obs, old, nuevo) ->
                actualizarCalendarioFiltro(nuevo));
    }

    // ─── Configuración de tabla ───────────────────────────────────────────────

    private void configurarColumnas() {
        colPaciente.setCellValueFactory(c ->
                new SimpleStringProperty("Paciente #" + c.getValue().getIdPaciente()));

        colMedico.setCellValueFactory(c -> {
            if (medicos != null) {
                return new SimpleStringProperty(medicos.stream()
                        .filter(m -> m.getIdMedico() == c.getValue().getIdMedico())
                        .map(m -> m.getNombre() + " " + m.getApellido())
                        .findFirst().orElse("Médico #" + c.getValue().getIdMedico()));
            }
            return new SimpleStringProperty("Médico #" + c.getValue().getIdMedico());
        });

        colFecha.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFecha() != null
                        ? c.getValue().getFecha().toString() : ""));

        colHora.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getHoraInicio() != null
                        ? c.getValue().getHoraInicio().toString() : ""));

        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnCancelar  = new Button("Cancelar");
            private final Button btnReagendar = new Button("Reagendar");
            private final HBox   hbox         = new HBox(8, btnCancelar, btnReagendar);

            {
                hbox.setAlignment(Pos.CENTER);
                btnCancelar.getStyleClass().add("button-logout");   // rojo
                btnCancelar.setStyle("-fx-padding: 6 12; -fx-font-size: 11px;");
                btnReagendar.setStyle("-fx-padding: 6 12; -fx-font-size: 11px;");
                btnCancelar.setOnAction(e  -> onCancelarCita(getTableView().getItems().get(getIndex())));
                btnReagendar.setOnAction(e -> onReagendarCita(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    // ─── Carga de datos ───────────────────────────────────────────────────────

    private void cargarMedicos() {
        try {
            String json = ClienteHttp.get("/api/medicos/activos");
            medicos = ClienteHttp.parsearLista(json, MedicoDTO.class);

            cbDoctorFiltro.setItems(FXCollections.observableArrayList(medicos));
            cbDoctorFiltro.getItems().add(0, null);
            cbDoctorFiltro.setConverter(new StringConverter<>() {
                public String toString(MedicoDTO m)   { return m == null ? "Todos los médicos" : m.toString(); }
                public MedicoDTO fromString(String s) { return null; }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Actualiza el DayCellFactory del DatePicker de filtro según el médico seleccionado.
     * Si no hay médico, deshabilita solo fechas sin jornada de ningún médico.
     * Si hay médico, deshabilita días sin jornada de ese médico.
     */
    private void actualizarCalendarioFiltro(MedicoDTO medico) {
        List<String> diasHabilitados;
        try {
            if (medico != null) {
                // Días con jornada del médico específico
                String json = ClienteHttp.get("/api/jornadas/medico/" + medico.getIdMedico() + "/dias");
                diasHabilitados = ClienteHttp.parsearLista(json, String.class);
            } else {
                // Todos los días que tienen jornada de cualquier médico
                String json = ClienteHttp.get("/api/jornadas");
                List<JornadaDTO> jornadas = ClienteHttp.parsearLista(json, JornadaDTO.class);
                diasHabilitados = jornadas.stream()
                        .map(JornadaDTO::getDiaSemana)
                        .filter(d -> d != null)
                        .distinct()
                        .toList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            diasHabilitados = List.of();
        }

        final List<String> dias = diasHabilitados;
        dpFechaFiltro.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                if (dias.isEmpty()) return; // sin info, no deshabilitar
                String nombreDia = traducirDia(d.getDayOfWeek().name());
                boolean sinJornada = dias.stream().noneMatch(j -> j.equalsIgnoreCase(nombreDia));
                setDisable(sinJornada);
                if (sinJornada)
                    setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #bbb;");
                else
                    setStyle("");
            }
        });
    }

    /**
     * Carga citas activas (no canceladas) con el filtro dado.
     * Guarda el filtro para poder recargar después de cancelar/reagendar.
     */
    private void cargarCitas(Integer idMedico, LocalDate fecha) {
        filtroIdMedico = idMedico;
        filtroFecha    = fecha;
        try {
            String url = "/api/citas?fecha=" + (fecha != null ? fecha : LocalDate.now());
            if (idMedico != null) url += "&idMedico=" + idMedico;
            String json = ClienteHttp.get(url);
            List<CitaDTO> citas = ClienteHttp.parsearLista(json, CitaDTO.class);
            tblCitas.setItems(FXCollections.observableArrayList(citas));
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** Recarga manteniendo el filtro activo. */
    private void recargarCitas() {
        cargarCitas(filtroIdMedico, filtroFecha);
    }

    private void actualizarContadores() {
        try {
            String json = ClienteHttp.get("/api/citas?fecha=" + LocalDate.now());
            List<CitaDTO> citasHoy = ClienteHttp.parsearLista(json, CitaDTO.class);

            long pendientes = citasHoy.stream()
                    .filter(c -> c.getIdEstadoCita() != null && c.getIdEstadoCita() == 2)
                    .count();

            if (lblTotalHoy != null)
                lblTotalHoy.setText(String.valueOf(citasHoy.size()));
            if (lblPendientesConfirmacionHoy != null)
                lblPendientesConfirmacionHoy.setText(String.valueOf(pendientes));
            if (medicos != null && medicosActivos != null)
                medicosActivos.setText(String.valueOf(medicos.size()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ─── Acciones de filtro y navegación ─────────────────────────────────────

    @FXML
    void onFiltrar(ActionEvent e) {
        LocalDate fecha  = dpFechaFiltro.getValue();
        MedicoDTO medico = cbDoctorFiltro.getValue();
        cargarCitas(medico != null ? medico.getIdMedico() : null, fecha);
    }

    @FXML
    void onNuevaCita(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/presentacion/vistas/VistaAgendarCita.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tblCitas.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @FXML
    void onCerrarSesion(ActionEvent e) {
        SesionUsuario.getInstancia().limpiarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/presentacion/vistas/VistaLogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tblCitas.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // ─── Cancelar cita ────────────────────────────────────────────────────────

    private void onCancelarCita(CitaDTO cita) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar cancelación");
        confirm.setHeaderText("¿Cancelar esta cita?");
        confirm.setContentText("Paciente #" + cita.getIdPaciente()
                + "  —  " + cita.getFecha() + " " + cita.getHoraInicio());

        ButtonType btnSi = new ButtonType("Sí, cancelar");
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnSi, btnNo);

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == btnSi) {
                try {
                    // DELETE /api/citas/{id}  →  cambia estado a Cancelada, no borra
                    ClienteHttp.delete("/api/citas/" + cita.getIdCita(), null);
                    mostrarInfo("Cita cancelada correctamente.");
                    recargarCitas();          // la cita ya no aparece (estado=Cancelada)
                    actualizarContadores();
                } catch (Exception ex) {
                    mostrarError("Error al cancelar: " + ex.getMessage());
                }
            }
        });
    }

    // ─── Reagendar cita ───────────────────────────────────────────────────────

    private void onReagendarCita(CitaDTO cita) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reagendar Cita");
        dialog.setHeaderText("Paciente #" + cita.getIdPaciente()
                + "  —  Médico #" + cita.getIdMedico());

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        // ── Fecha ──
        Label lblFecha = new Label("Nueva Fecha:");
        DatePicker dpNuevaFecha = new DatePicker(LocalDate.now().plusDays(1));

        // Deshabilitar días sin jornada del médico y fechas pasadas
        List<String> diasConJornada = obtenerDiasConJornada(cita.getIdMedico());
        dpNuevaFecha.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                String nombreDia = traducirDia(d.getDayOfWeek().name());
                boolean sinJornada = !diasConJornada.isEmpty()
                        && diasConJornada.stream().noneMatch(j -> j.equalsIgnoreCase(nombreDia));
                setDisable(d.isBefore(LocalDate.now()) || sinJornada);
                if (sinJornada && !d.isBefore(LocalDate.now()))
                    setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #aaa;");
            }
        });

        // ── Hora ──
        Label lblHora = new Label("Nueva Hora:");
        ComboBox<LocalTime> cbNuevaHora = new ComboBox<>();
        cbNuevaHora.setPromptText("Seleccione una hora");
        cbNuevaHora.setPrefWidth(220);

        // Cargar horas al cambiar fecha
        dpNuevaFecha.valueProperty().addListener((obs, old, nueva) -> {
            if (nueva != null) cargarHorasDisponibles(cita.getIdMedico(), nueva, cbNuevaHora);
        });
        // Cargar horas para la fecha inicial
        cargarHorasDisponibles(cita.getIdMedico(), dpNuevaFecha.getValue(), cbNuevaHora);

        content.getChildren().addAll(lblFecha, dpNuevaFecha, lblHora, cbNuevaHora);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;

            LocalDate nuevaFecha = dpNuevaFecha.getValue();
            LocalTime nuevaHora  = cbNuevaHora.getValue();

            if (nuevaFecha == null || nuevaHora == null) {
                mostrarError("Debe seleccionar fecha y hora.");
                return;
            }

            try {
                // PATCH /api/citas/{id}/reagendar
                String respuesta = ClienteHttp.patch(
                        "/api/citas/" + cita.getIdCita() + "/reagendar",
                        Map.of("nuevaFecha", nuevaFecha.toString(),
                               "nuevaHora",  nuevaHora.toString()),
                        null);

                if (respuesta != null && respuesta.contains("reagendada")) {
                    mostrarInfo("Cita reagendada correctamente.");
                    recargarCitas();
                } else {
                    mostrarError("No se pudo reagendar: " + respuesta);
                }
            } catch (Exception ex) {
                mostrarError("Error al reagendar: " + ex.getMessage());
            }
        });
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void cargarHorasDisponibles(int idMedico, LocalDate fecha, ComboBox<LocalTime> cbHora) {
        cbHora.getItems().clear();
        if (fecha == null) return;
        try {
            String json = ClienteHttp.get(
                    "/api/citas/disponibilidad?idMedico=" + idMedico + "&fecha=" + fecha);
            List<LocalTime> horarios = ClienteHttp.parsearLista(json, LocalTime.class);
            cbHora.getItems().addAll(horarios);
            if (!horarios.isEmpty()) cbHora.setValue(horarios.get(0));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private List<String> obtenerDiasConJornada(int idMedico) {
        try {
            String json = ClienteHttp.get("/api/jornadas/medico/" + idMedico + "/dias");
            return ClienteHttp.parsearLista(json, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /** Convierte el nombre en inglés del DayOfWeek al español usado en la BD. */
    private String traducirDia(String dayOfWeekEn) {
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

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Información"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
