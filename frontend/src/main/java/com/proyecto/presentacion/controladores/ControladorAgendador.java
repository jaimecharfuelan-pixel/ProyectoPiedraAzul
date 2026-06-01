package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.facade.BackendFacade;
import com.proyecto.presentacion.util.CalendarioTurnosHelper;
import com.proyecto.presentacion.util.EstadoCita;
import com.proyecto.presentacion.dto.CitaDTO;
import com.proyecto.presentacion.dto.ErrorValidacionDTO;
import com.proyecto.presentacion.dto.HistorialCitaDTO;
import com.proyecto.presentacion.dto.MedicoDTO;
import com.proyecto.presentacion.dto.PersonaDTO;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
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
    @FXML private TableColumn<CitaDTO, Void>   colEstado;
    @FXML private TableColumn<CitaDTO, Void>   colHistorial;
    @FXML private TableColumn<CitaDTO, Void>   colAcciones;
    @FXML private Label lblTotalHoy;
    @FXML private Label lblPendientesConfirmacionHoy;
    @FXML private Label medicosActivos;

    // Filtro activo — se mantiene al recargar tras cancelar/reagendar
    private Integer filtroIdMedico = null;
    private LocalDate filtroFecha  = null;

    private List<MedicoDTO> medicos;
    private final Map<Integer, String> mapaPacientes = new HashMap<>();
    private final BackendFacade backend = new BackendFacade();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarMedicos();
        cargarPacientes();
        // Sin filtro de fecha inicial — muestra todas las citas de hoy o las más recientes
        filtroFecha = null;
        cargarCitas(null, null);
        actualizarContadores();
        // Calendario inicial: días con jornada de cualquier médico
        actualizarCalendarioFiltro(null);
        // Cuando cambia el médico, actualizar el calendario del filtro
        cbDoctorFiltro.valueProperty().addListener((obs, old, nuevo) ->
                actualizarCalendarioFiltro(nuevo));
    }

    // ─── Configuración de tabla ───────────────────────────────────────────────

    private void configurarColumnas() {
        colPaciente.setCellValueFactory(c -> {
            String nombre = mapaPacientes.get(c.getValue().getIdPaciente());
            return new SimpleStringProperty(nombre != null ? nombre : "Paciente #" + c.getValue().getIdPaciente());
        });

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

        // ── Columna de Estado (con badge visual y color) ──
        colEstado.setCellFactory(param -> new TableCell<>() {
            private final Label lblEstado = new Label();

            {
                lblEstado.setPrefWidth(120);
                lblEstado.setWrapText(false);
                lblEstado.setStyle("-fx-alignment: center; -fx-padding: 8px 12px; -fx-background-radius: 6;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView() == null) {
                    setGraphic(null);
                    return;
                }
                CitaDTO cita = getTableView().getItems().get(getIndex());
                if (cita == null) {
                    setGraphic(null);
                    return;
                }
                // Obtener información del estado
                Integer idEstado = cita.getIdEstadoCita();
                String icono = EstadoCita.getIcono(idEstado);
                String nombre = EstadoCita.getNombre(idEstado);
                String tooltip = EstadoCita.getDescripcion(idEstado);
                String css = EstadoCita.generarEstiloCSS(idEstado);

                // Configurar el label con ícono y nombre
                lblEstado.setText(icono + " " + nombre);
                lblEstado.setStyle(css);
                lblEstado.setTooltip(new Tooltip(tooltip));

                setGraphic(lblEstado);
            }
        });

        // ── Columna de Historial (con botón para ver detalles) ──
        // Patrón elegido: Diálogo modal al hacer click en el botón "Ver Historial"
        // Justificación: Los sistemas médicos modernos (Epic, Cerner) usan diálogos modales
        // para mostrar auditoría de cambios, mantiene la vista limpia y no consume espacio horizontal.
        colHistorial.setCellFactory(param -> new TableCell<>() {
            private final Button btnHistorial = new Button("Ver Historial");

            {
                btnHistorial.setStyle("-fx-padding: 6 12; -fx-font-size: 10px;");
                btnHistorial.setOnAction(e -> onVerHistorial(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnHistorial);
            }
        });

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
            medicos = backend.listarMedicosActivos();
            cbDoctorFiltro.setItems(FXCollections.observableArrayList(medicos));
            cbDoctorFiltro.getItems().add(0, null);
            cbDoctorFiltro.setConverter(new StringConverter<>() {
                public String toString(MedicoDTO m)   { return m == null ? "Todos los médicos" : m.toString(); }
                public MedicoDTO fromString(String s) { return null; }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarPacientes() {
        try {
            List<PersonaDTO> pacientes = backend.listarPacientes();
            mapaPacientes.clear();
            for (PersonaDTO p : pacientes)
                mapaPacientes.put(p.getIdPersona(), p.getNombre() + " " + p.getApellido());
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Actualiza el DayCellFactory del DatePicker de filtro según el médico seleccionado.
     * Si no hay médico, deshabilita solo fechas sin jornada de ningún médico.
     * Si hay médico, deshabilita días sin jornada de ese médico.
     */
    private void actualizarCalendarioFiltro(MedicoDTO medico) {
        try {
            List<String> diasHabilitados = (medico != null)
                    ? backend.listarDiasConJornada(medico)
                    : backend.listarDiasConJornadaTodos();
            CalendarioTurnosHelper.aplicarCalendarioPorTurnos(dpFechaFiltro, diasHabilitados, false);
        } catch (Exception e) {
            e.printStackTrace();
            CalendarioTurnosHelper.aplicarCalendarioPorTurnos(dpFechaFiltro, List.of(), false);
        }
    }

    /**
     * Carga citas activas (no canceladas) con el filtro dado.
     * Si fecha es null, carga todas las citas sin filtrar por fecha.
     */
    private void cargarCitas(Integer idMedico, LocalDate fecha) {
        filtroIdMedico = idMedico;
        filtroFecha    = fecha;
        try {
            List<CitaDTO> citas = backend.listarCitas(fecha, idMedico);
            if (idMedico != null && fecha == null) {
                final int id = idMedico;
                citas = citas.stream().filter(c -> c.getIdMedico() == id).toList();
            }
            tblCitas.setItems(FXCollections.observableArrayList(citas));
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** Recarga manteniendo el filtro activo. */
    private void recargarCitas() {
        cargarCitas(filtroIdMedico, filtroFecha);
    }

    private void actualizarContadores() {
        try {
            List<CitaDTO> citasHoy = backend.listarCitasHoy();
            long pendientes = citasHoy.stream()
                    .filter(c -> c.getIdEstadoCita() != null && c.getIdEstadoCita() == 2)
                    .count();
            if (lblTotalHoy != null)                lblTotalHoy.setText(String.valueOf(citasHoy.size()));
            if (lblPendientesConfirmacionHoy != null) lblPendientesConfirmacionHoy.setText(String.valueOf(pendientes));
            if (medicos != null && medicosActivos != null) medicosActivos.setText(String.valueOf(medicos.size()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ─── Acciones de filtro y navegación ─────────────────────────────────────

    @FXML
    void onFiltrar(ActionEvent e) {
        LocalDate fecha  = dpFechaFiltro.getValue();   // puede ser null si no seleccionó
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

    @FXML
    void onExportar(ActionEvent e) {
        List<CitaDTO> citas = tblCitas.getItems();
        if (citas == null || citas.isEmpty()) {
            mostrarInfo("No hay citas para exportar con el filtro actual.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar exportación de citas");
        fileChooser.setInitialFileName("citas_exportadas.csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo CSV (*.csv)", "*.csv"));

        Stage stage = (Stage) tblCitas.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8))) {

            // BOM para que Excel reconozca UTF-8 correctamente
            writer.write('\uFEFF');

            // Encabezados
            writer.write("ID Cita;Paciente;Médico;Fecha;Hora;Estado");
            writer.newLine();

            for (CitaDTO cita : citas) {
                String paciente = mapaPacientes.getOrDefault(cita.getIdPaciente(),
                        "Paciente #" + cita.getIdPaciente());

                String medico = "Médico #" + cita.getIdMedico();
                if (medicos != null) {
                    medico = medicos.stream()
                            .filter(m -> m.getIdMedico() == cita.getIdMedico())
                            .map(m -> m.getNombre() + " " + m.getApellido())
                            .findFirst().orElse(medico);
                }

                String fecha = cita.getFecha() != null ? cita.getFecha().toString() : "";
                String hora  = cita.getHoraInicio() != null ? cita.getHoraInicio().toString() : "";
                String estado = traducirEstadoCita(cita.getIdEstadoCita());

                writer.write(String.join(";",
                        escaparCsv(String.valueOf(cita.getIdCita())),
                        escaparCsv(paciente),
                        escaparCsv(medico),
                        escaparCsv(fecha),
                        escaparCsv(hora),
                        escaparCsv(estado)));
                writer.newLine();
            }

            mostrarInfo("✅ Exportación completada: " + citas.size() + " cita(s) guardadas en\n" + archivo.getAbsolutePath());

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al exportar: " + ex.getMessage());
        }
    }

    /**
     * Escapa un valor para CSV: si contiene punto y coma, comillas o saltos de línea
     * lo envuelve entre comillas dobles y duplica las comillas internas.
     */
    private String escaparCsv(String valor) {
        if (valor == null) return "";
        if (valor.contains(";") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    /**
     * Convierte el ID numérico del estado de cita a su nombre legible.
     * 1=Cancelada, 2=Pendiente, 3=Confirmada, 4=Completada, 5=No Asistió
     */
    private String traducirEstadoCita(Integer idEstado) {
        if (idEstado == null) return "";
        return switch (idEstado) {
            case 1 -> "Cancelada";
            case 2 -> "Pendiente";
            case 3 -> "Confirmada";
            case 4 -> "Completada";
            case 5 -> "No Asistió";
            default -> "Estado #" + idEstado;
        };
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
                    backend.cancelarCita(cita.getIdCita(), null);
                    mostrarInfo("Cita cancelada correctamente.");
                    recargarCitas();
                    actualizarContadores();
                } catch (Exception ex) { mostrarError("Error al cancelar: " + ex.getMessage()); }
            }
        });
    }

    // ─── Reagendar cita ───────────────────────────────────────────────────────

    private void onReagendarCita(CitaDTO cita) {
        MedicoDTO medicoCita = medicos.stream()
                .filter(m -> m.getIdMedico() == cita.getIdMedico())
                .findFirst().orElse(null);
        String nombreMedico = medicoCita != null
                ? medicoCita.getNombre() + " " + medicoCita.getApellido()
                : "Médico #" + cita.getIdMedico();
        String nombrePaciente = mapaPacientes.getOrDefault(cita.getIdPaciente(),
                "Paciente #" + cita.getIdPaciente());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reagendar Cita");
        dialog.setHeaderText(nombrePaciente + "  —  " + nombreMedico);

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label lblFecha = new Label("Nueva Fecha:");
        DatePicker dpNuevaFecha = new DatePicker(
                cita.getFecha() != null && !cita.getFecha().isBefore(LocalDate.now())
                        ? cita.getFecha() : LocalDate.now().plusDays(1));

        List<String> diasConJornada = obtenerDiasConJornada(medicoCita, cita.getIdMedico());
        CalendarioTurnosHelper.aplicarCalendarioPorTurnos(dpNuevaFecha, diasConJornada, true);

        Label lblHora = new Label("Nueva Hora:");
        ComboBox<LocalTime> cbNuevaHora = new ComboBox<>();
        cbNuevaHora.setPrefWidth(220);
        CalendarioTurnosHelper.configurarComboHoras(cbNuevaHora);

        dpNuevaFecha.valueProperty().addListener((obs, old, nueva) -> {
            if (nueva != null) {
                cargarHorasDisponibles(cita.getIdMedico(), nueva, cbNuevaHora, cita.getIdCita());
            }
        });
        cargarHorasDisponibles(cita.getIdMedico(), dpNuevaFecha.getValue(), cbNuevaHora, cita.getIdCita());

        content.getChildren().addAll(lblFecha, dpNuevaFecha, lblHora, cbNuevaHora);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;
            LocalDate nuevaFecha = dpNuevaFecha.getValue();
            LocalTime nuevaHora  = cbNuevaHora.getValue();
            if (nuevaFecha == null || nuevaHora == null) {
                mostrarError("Debe seleccionar fecha y hora disponibles según el turno del médico.");
                return;
            }
            try {
                ErrorValidacionDTO resultado = backend.reagendarCitaConValidacion(
                        cita.getIdCita(), nuevaFecha, nuevaHora, null);
                if (resultado.isExitosa()) {
                    mostrarInfo("✅ Cita reagendada correctamente.");
                    recargarCitas();
                } else {
                    mostrarError(formatearErroresValidacion(resultado));
                }
            } catch (Exception ex) {
                mostrarError("❌ Error al reagendar: " + ex.getMessage());
            }
        });
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void cargarHorasDisponibles(int idMedico, LocalDate fecha,
                                        ComboBox<LocalTime> cbHora, Integer excluirCitaId) {
        if (fecha == null) return;
        try {
            List<LocalTime> horarios = backend.consultarDisponibilidad(idMedico, fecha, excluirCitaId);
            CalendarioTurnosHelper.cargarHorasEnCombo(cbHora, horarios);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private List<String> obtenerDiasConJornada(MedicoDTO medico, int idMedicoFallback) {
        try {
            MedicoDTO m = medico;
            if (m == null && medicos != null) {
                m = medicos.stream()
                        .filter(x -> x.getIdMedico() == idMedicoFallback)
                        .findFirst().orElse(null);
            }
            if (m != null) return backend.listarDiasConJornada(m);
            return backend.listarDiasConJornada(idMedicoFallback);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
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

    private String formatearErroresValidacion(ErrorValidacionDTO dto) {
        if (dto == null) return "⚠️ No se pudo completar la validación.";
        if (dto.getErrores() == null || dto.getErrores().isEmpty()) {
            return "⚠️ " + (dto.getMensajePrincipal() != null ? dto.getMensajePrincipal() : "No se pudo reagendar la cita.");
        }
        return "⚠️ " + (dto.getMensajePrincipal() != null ? dto.getMensajePrincipal() : "No se pudo reagendar la cita.")
                + "\n\n• " + String.join("\n• ", dto.getErrores());
    }

    // ─── Mostrar Historial de Cambios ──────────────────────────────────────────
    
    /**
     * Abre un diálogo modal que muestra el historial completo de cambios de la cita.
     * Muestra: reagendamientos (fecha orig → nueva), cancelaciones (desde cuándo), 
     * cambios de estado y quién realizó cada cambio.
     */
    private void onVerHistorial(CitaDTO cita) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Historial de Cambios - Cita #" + cita.getIdCita());
        dialog.setHeaderText("Paciente #" + cita.getIdPaciente() + "  —  Médico #" + cita.getIdMedico());

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 15;");

        // Intentar cargar el historial
        try {
            List<HistorialCitaDTO> historial = backend.obtenerHistorialCita(cita.getIdCita());
            
            if (historial == null || historial.isEmpty()) {
                Label lblSinHistorial = new Label("No hay cambios registrados para esta cita.");
                lblSinHistorial.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
                content.getChildren().add(lblSinHistorial);
            } else {
                // Mostrar cada cambio en una fila con detalles
                for (HistorialCitaDTO cambio : historial) {
                    VBox rowCambio = new VBox(5);
                    rowCambio.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 0 1 0; -fx-padding: 8;");

                    String tipo = cambio.getTipoCambio() != null ? cambio.getTipoCambio() : "";
                    String anterior = cambio.getValorAnterior() != null ? cambio.getValorAnterior() : "";
                    String nuevo = cambio.getValorNuevo() != null ? cambio.getValorNuevo() : "";

                    Label lblTipo = new Label("📋 " + tipo);
                    lblTipo.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

                    Label lblDetalles;
                    if ("REAGENDAMIENTO".equals(tipo)) {
                        lblDetalles = new Label("📅 " + anterior + " → " + nuevo);
                    } else if ("CANCELACION".equals(tipo)) {
                        lblDetalles = new Label("❌ " + anterior + " → " + nuevo);
                    } else {
                        lblDetalles = new Label("⚙️ " + anterior + " → " + nuevo);
                    }
                    lblDetalles.setStyle("-fx-font-size: 11px;");

                    Label lblFechaUsuario = new Label("🕐 " + cambio.getFechaHora() + "  |  Usuario: #" + cambio.getIdUsuario());
                    lblFechaUsuario.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");

                    rowCambio.getChildren().addAll(lblTipo, lblDetalles, lblFechaUsuario);
                    content.getChildren().add(rowCambio);
                }
            }
        } catch (Exception ex) {
            Label lblError = new Label("Error al cargar historial: " + ex.getMessage());
            lblError.setStyle("-fx-text-fill: #c00;");
            content.getChildren().add(lblError);
        }

        // Scroll si hay muchos cambios
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }
}
