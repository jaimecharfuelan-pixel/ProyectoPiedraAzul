package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.facade.BackendFacade;
import com.proyecto.presentacion.util.CalendarioTurnosHelper;
import com.proyecto.presentacion.util.EstadoCita;
import com.proyecto.presentacion.dto.CitaDTO;
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

/**
 * Controlador para la vista del Médico (Fase 3 - RF-04).
 * 
 * Responsabilidades:
 * - Mostrar citas propias del médico logueado
 * - Filtrar por fecha y estado de cita
 * - Permitir cambiar el estado de la cita
 * - Visualizar información del paciente
 * - Mostrar estadísticas rápidas
 */
public class ControladorMedico implements Initializable {

    @FXML private DatePicker              dpFechaFiltro;
    @FXML private ComboBox<String>        cbEstadoFiltro;
    @FXML private TableView<CitaDTO>      tblMisCitas;
    @FXML private TableColumn<CitaDTO, String> colPaciente;
    @FXML private TableColumn<CitaDTO, String> colFecha;
    @FXML private TableColumn<CitaDTO, String> colHora;
    @FXML private TableColumn<CitaDTO, String> colEspecialidad;
    @FXML private TableColumn<CitaDTO, Void>   colEstado;
    @FXML private TableColumn<CitaDTO, Void>   colAcciones;
    @FXML private Label lblCitasHoy;
    @FXML private Label lblCitasPendientes;
    @FXML private Label lblPacientesHoy;

    private MedicoDTO medicoActual;
    private List<CitaDTO> todasLasCitas;
    private final Map<Integer, String> mapaPacientes    = new HashMap<>();
    private final Map<Integer, String> mapaEspecialidades = new HashMap<>();
    private final BackendFacade backend = new BackendFacade();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            cargarDatosMedicoActual();
            configurarColumnas();
            cargarPacientes();
            cargarEspecialidades();
            cargarEstadosDisponibles();
            cargarCitas(null, null);
            actualizarContadores();
            actualizarCalendarioFiltro();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al inicializar", e.getMessage());
        }
    }

    // ─── Carga de datos iniciales ──────────────────────────────────────────────

    /**
     * Obtiene los datos del médico asociado al usuario logueado.
     * Utiliza el idUsuario de SesionUsuario para buscar el médico.
     */
    private void cargarDatosMedicoActual() throws Exception {
        int idUsuario = SesionUsuario.getInstancia().getIdUsuario();
        if (idUsuario <= 0) {
            throw new RuntimeException("No hay usuario logueado");
        }
        
        // Listar todos los médicos y buscar el que coincida con el idUsuario
        List<MedicoDTO> medicos = backend.listarMedicosActivos();
        medicoActual = medicos.stream()
                .filter(m -> m.getIdUsuario() != null && m.getIdUsuario() == idUsuario)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró médico asociado al usuario actual"));
    }

    private void cargarPacientes() {
        try {
            List<PersonaDTO> pacientes = backend.listarPacientes();
            mapaPacientes.clear();
            for (PersonaDTO p : pacientes) {
                mapaPacientes.put(p.getIdPersona(), p.getNombre() + " " + p.getApellido());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEspecialidades() {
        try {
            List<Map> especialidades = backend.listarEspecialidades();
            mapaEspecialidades.clear();
            for (Map esp : especialidades) {
                Object id     = esp.get("idEspecialidad");
                Object nombre = esp.get("nombre");
                if (id != null && nombre != null) {
                    mapaEspecialidades.put(((Number) id).intValue(), nombre.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEstadosDisponibles() {
        cbEstadoFiltro.setItems(FXCollections.observableArrayList(
                "",
                "Cancelada",
                "Pendiente",
                "Confirmada",
                "Completada",
                "No Asistió"
        ));
        cbEstadoFiltro.setValue("");
    }

    // ─── Configuración de tabla ───────────────────────────────────────────────

    private void configurarColumnas() {
        // ── Paciente ──
        colPaciente.setCellValueFactory(c -> {
            String nombre = mapaPacientes.get(c.getValue().getIdPaciente());
            return new SimpleStringProperty(nombre != null ? nombre : "Paciente #" + c.getValue().getIdPaciente());
        });

        // ── Fecha ──
        colFecha.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFecha() != null
                        ? c.getValue().getFecha().toString() : ""));

        // ── Hora ──
        colHora.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getHoraInicio() != null
                        ? c.getValue().getHoraInicio().toString() : ""));

        // ── Especialidad ──
        colEspecialidad.setCellValueFactory(c -> {
            String nombre = mapaEspecialidades.get(medicoActual.getIdEspecialidad());
            return new SimpleStringProperty(
                    nombre != null ? nombre : "Especialidad #" + medicoActual.getIdEspecialidad());
        });

        // ── Estado (con badge visual) ──
        colEstado.setCellFactory(param -> new TableCell<>() {
            private final Label lblEstado = new Label();

            {
                lblEstado.setMinWidth(110);
                lblEstado.setPrefWidth(130);
                lblEstado.setWrapText(false);
                lblEstado.setStyle("-fx-alignment: center; -fx-padding: 6px 14px; -fx-background-radius: 6;");
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

                Integer idEstado = cita.getIdEstadoCita();
                String icono = EstadoCita.getIcono(idEstado);
                String nombre = EstadoCita.getNombre(idEstado);
                String tooltip = EstadoCita.getDescripcion(idEstado);
                String css = EstadoCita.generarEstiloCSS(idEstado);

                lblEstado.setText(icono + " " + nombre);
                lblEstado.setStyle(css);
                lblEstado.setTooltip(new Tooltip(tooltip));

                setGraphic(lblEstado);
            }
        });

        // ── Acciones ──
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnConfirmar = new Button("Confirmar");
            private final Button btnCancelar  = new Button("Cancelar");
            private final Button btnCompletar = new Button("Completar");
            private final Button btnReagendar = new Button("Reagendar");
            private final HBox   hbox         = new HBox(6, btnConfirmar, btnCancelar, btnCompletar, btnReagendar);

            {
                hbox.setAlignment(Pos.CENTER);
                btnConfirmar.setStyle("-fx-padding: 6 12; -fx-font-size: 12px;");
                btnCancelar.getStyleClass().add("button-logout");
                btnCancelar.setStyle("-fx-padding: 6 12; -fx-font-size: 12px;");
                btnCompletar.getStyleClass().add("button-export");
                btnCompletar.setStyle("-fx-padding: 6 12; -fx-font-size: 12px;");
                btnReagendar.setStyle("-fx-padding: 6 12; -fx-font-size: 12px;");

                btnConfirmar.setOnAction(e -> onCambiarEstado(getTableView().getItems().get(getIndex()), 3));
                btnCancelar.setOnAction(e -> onCambiarEstado(getTableView().getItems().get(getIndex()), 1));
                btnCompletar.setOnAction(e -> onCambiarEstado(getTableView().getItems().get(getIndex()), 4));
                btnReagendar.setOnAction(e -> onReagendar(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    // ─── Carga de citas ───────────────────────────────────────────────────────

    private void cargarCitas(LocalDate fecha, String estado) {
        try {
            // Cargar TODAS las citas del médico
            todasLasCitas = backend.listarCitas(null, medicoActual.getIdMedico());

            // Filtrar por fecha si se especifica
            if (fecha != null) {
                todasLasCitas = todasLasCitas.stream()
                        .filter(c -> c.getFecha().equals(fecha))
                        .toList();
            }

            // Filtrar por estado si se especifica
            if (estado != null && !estado.isEmpty()) {
                int idEstado = mapearEstadoAlId(estado);
                todasLasCitas = todasLasCitas.stream()
                        .filter(c -> c.getIdEstadoCita() == idEstado)
                        .toList();
            }

            tblMisCitas.setItems(FXCollections.observableArrayList(todasLasCitas));
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar citas", e.getMessage());
        }
    }

    private void actualizarCalendarioFiltro() {
        try {
            List<String> diasConCitas = todasLasCitas.stream()
                    .map(c -> c.getFecha().getDayOfWeek().toString())
                    .distinct()
                    .map(day -> convertirDiaAlEspanol(day))
                    .toList();
            CalendarioTurnosHelper.aplicarCalendarioPorTurnos(dpFechaFiltro, diasConCitas, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarContadores() {
        try {
            LocalDate hoy = LocalDate.now();
            
            long citasHoy = todasLasCitas.stream()
                    .filter(c -> c.getFecha().equals(hoy))
                    .count();
            
            long citasPendientes = todasLasCitas.stream()
                    .filter(c -> c.getIdEstadoCita() == EstadoCita.ID_PENDIENTE)
                    .count();
            
            long pacientesHoy = todasLasCitas.stream()
                    .filter(c -> c.getFecha().equals(hoy))
                    .map(CitaDTO::getIdPaciente)
                    .distinct()
                    .count();

            lblCitasHoy.setText(String.valueOf(citasHoy));
            lblCitasPendientes.setText(String.valueOf(citasPendientes));
            lblPacientesHoy.setText(String.valueOf(pacientesHoy));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ─── Acciones del usuario ─────────────────────────────────────────────────

    @FXML
    private void onFiltrar() {
        try {
            LocalDate fecha = dpFechaFiltro.getValue();
            String estado = cbEstadoFiltro.getValue();
            cargarCitas(fecha, estado);
            actualizarContadores();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al filtrar", e.getMessage());
        }
    }

    @FXML
    private void onNuevaCita(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/presentacion/vistas/VistaAgendarCita.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tblMisCitas.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error", "No se pudo cargar la vista de agendar cita");
        }
    }

    @FXML
    private void onExportar(ActionEvent e) {
        List<CitaDTO> citas = tblMisCitas.getItems();
        if (citas == null || citas.isEmpty()) {
            mostrarInfo("No hay citas para exportar con el filtro actual.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar exportación de citas");
        fileChooser.setInitialFileName("mis_citas_exportadas.csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo CSV (*.csv)", "*.csv"));

        Stage stage = (Stage) tblMisCitas.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8))) {

            // BOM para que Excel reconozca UTF-8 correctamente
            writer.write('\uFEFF');

            // Encabezados
            writer.write("ID Cita;Paciente;Fecha;Hora;Especialidad;Estado");
            writer.newLine();

            for (CitaDTO cita : citas) {
                String paciente = mapaPacientes.getOrDefault(cita.getIdPaciente(),
                        "Paciente #" + cita.getIdPaciente());
                String fecha = cita.getFecha() != null ? cita.getFecha().toString() : "";
                String hora = cita.getHoraInicio() != null ? cita.getHoraInicio().toString() : "";
                String especialidad = mapaEspecialidades.getOrDefault(
                        medicoActual.getIdEspecialidad(),
                        "Especialidad #" + medicoActual.getIdEspecialidad());
                String estado = EstadoCita.getNombre(cita.getIdEstadoCita());

                writer.write(String.join(";",
                        escaparCsv(String.valueOf(cita.getIdCita())),
                        escaparCsv(paciente),
                        escaparCsv(fecha),
                        escaparCsv(hora),
                        escaparCsv(especialidad),
                        escaparCsv(estado)));
                writer.newLine();
            }

            mostrarExito("✅ Exportación completada: " + citas.size() + " cita(s) guardadas en\n" + archivo.getAbsolutePath());

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al exportar", ex.getMessage());
        }
    }

    private void onCambiarEstado(CitaDTO cita, int nuevoEstado) {
        try {
            cita.setIdEstadoCita(nuevoEstado);
            String token = SesionUsuario.getInstancia().getToken();
            backend.editarCita(cita, token);
            
            // Recargar citas
            cargarCitas(dpFechaFiltro.getValue(), cbEstadoFiltro.getValue());
            actualizarContadores();
            
            String nombreEstado = EstadoCita.getNombre(nuevoEstado);
            mostrarExito("Cita actualizada a: " + nombreEstado);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cambiar estado", e.getMessage());
        }
    }

    private void onReagendar(CitaDTO cita) {
        try {
            // Guardar la cita en una variable estática temporal para acceder desde VistaAgendarCita
            // Si tienes un controlador compartido o algún mecanismo, úsalo aquí
            // Por ahora, simplemente navegamos a VistaAgendarCita
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/presentacion/vistas/VistaAgendarCita.fxml"));
            Parent root = loader.load();
            
            // Si VistaAgendarCita tiene controlador, podrías pasarle la cita:
            // ControladorAgendarCita controller = loader.getController();
            // controller.setCitaAReagendar(cita);
            
            Stage stage = (Stage) tblMisCitas.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar la vista para reagendar");
        }
    }

    @FXML
    private void onCerrarSesion() {
        try {
            SesionUsuario.getInstancia().limpiarSesion();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/presentacion/vistas/VistaLogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tblMisCitas.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cerrar sesión", e.getMessage());
        }
    }

    // ─── Utilidades ───────────────────────────────────────────────────────────

    private String escaparCsv(String valor) {
        if (valor == null) valor = "";
        if (valor.contains(";") || valor.contains("\"") || valor.contains("\n")) {
            valor = valor.replace("\"", "\"\"");
            return "\"" + valor + "\"";
        }
        return valor;
    }

    private int mapearEstadoAlId(String estado) {
        return switch (estado) {
            case "Cancelada" -> EstadoCita.ID_CANCELADA;
            case "Pendiente" -> EstadoCita.ID_PENDIENTE;
            case "Confirmada" -> EstadoCita.ID_CONFIRMADA;
            case "Completada" -> EstadoCita.ID_COMPLETADA;
            case "No Asistió" -> EstadoCita.ID_NO_ASISTIO;
            default -> 0;
        };
    }

    private String convertirDiaAlEspanol(String day) {
        return switch (day) {
            case "MONDAY" -> "Lunes";
            case "TUESDAY" -> "Martes";
            case "WEDNESDAY" -> "Miércoles";
            case "THURSDAY" -> "Jueves";
            case "FRIDAY" -> "Viernes";
            case "SATURDAY" -> "Sábado";
            case "SUNDAY" -> "Domingo";
            default -> day;
        };
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText("Operación completada");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText("Información");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
