package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.facade.BackendFacade;
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
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ControladorPaciente implements Initializable {

    @FXML private Label                        lblBienvenida;
    @FXML private TableView<CitaDTO>           tblHistorial;
    @FXML private TableColumn<CitaDTO, String> colHistNomPaciente;
    @FXML private TableColumn<CitaDTO, String> colHistCedMedico;
    @FXML private TableColumn<CitaDTO, String> colHistNomMedico;
    @FXML private TableColumn<CitaDTO, String> colHistFecha;
    @FXML private TableColumn<CitaDTO, String> colHistHoraInicio;
    @FXML private TableColumn<CitaDTO, String> colHistHoraFin;
    @FXML private TableColumn<CitaDTO, Void>   colHistEstado;

    @FXML private TableView<CitaDTO>           tblFuturas;
    @FXML private TableColumn<CitaDTO, String> colFutNomPaciente;
    @FXML private TableColumn<CitaDTO, String> colFutCedMedico;
    @FXML private TableColumn<CitaDTO, String> colFutNomMedico;
    @FXML private TableColumn<CitaDTO, String> colFutFecha;
    @FXML private TableColumn<CitaDTO, String> colFutHoraInicio;
    @FXML private TableColumn<CitaDTO, String> colFutHoraFin;
    @FXML private TableColumn<CitaDTO, Void>   colFutEstado;

    // Estados que se consideran "terminados" → van a Historial sin importar la fecha
    private static final java.util.Set<Integer> ESTADOS_HISTORIAL =
            java.util.Set.of(
                EstadoCita.ID_COMPLETADA,   // 4
                EstadoCita.ID_CANCELADA,    // 1
                EstadoCita.ID_NO_ASISTIO    // 5
            );

    private int idPaciente;
    private PersonaDTO pacienteActual;
    private final Map<Integer, MedicoDTO>  mapaMedicos        = new HashMap<>();
    private final Map<Integer, String>     mapaCedulasMedicos = new HashMap<>();
    private final BackendFacade backend = new BackendFacade();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idPaciente = resolverIdPaciente();
        cargarMedicos();
        configurarColumnas(tblHistorial, colHistNomPaciente, colHistCedMedico,
                colHistNomMedico, colHistFecha, colHistHoraInicio, colHistHoraFin, colHistEstado);
        configurarColumnas(tblFuturas, colFutNomPaciente, colFutCedMedico,
                colFutNomMedico, colFutFecha, colFutHoraInicio, colFutHoraFin, colFutEstado);
        actualizarBienvenida();
        cargarTablas();
    }

    // ── Resolución de datos del paciente ─────────────────────────────────────

    private int resolverIdPaciente() {
        int idUsuario = SesionUsuario.getInstancia().getIdUsuario();
        if (idUsuario <= 0) return -1;
        try {
            List<PersonaDTO> personas = backend.listarPersonas();
            pacienteActual = personas.stream()
                    .filter(p -> p.getIdUsuario() != null && p.getIdUsuario() == idUsuario)
                    .findFirst().orElse(null);
            if (pacienteActual == null) return -1;
            SesionUsuario.getInstancia().setIdPacienteActual(pacienteActual.getIdPersona());
            return pacienteActual.getIdPersona();
        } catch (Exception e) { e.printStackTrace(); return -1; }
    }

    private void cargarMedicos() {
        try {
            List<MedicoDTO> medicos = backend.listarMedicosActivos();
            mapaMedicos.clear();
            for (MedicoDTO m : medicos) {
                mapaMedicos.put(m.getIdMedico(), m);
            }

            // Cruzar MedicoDTO.idUsuario → PersonaDTO.cedulaCiudadania
            // para mostrar la cédula real del médico en la columna
            List<PersonaDTO> personas = backend.listarPersonas();
            mapaCedulasMedicos.clear();
            for (MedicoDTO m : medicos) {
                if (m.getIdUsuario() == null) continue;
                personas.stream()
                        .filter(p -> p.getIdUsuario() != null && p.getIdUsuario().equals(m.getIdUsuario()))
                        .findFirst()
                        .ifPresent(p -> {
                            String cedula = p.getCedulaCiudadania();
                            mapaCedulasMedicos.put(m.getIdMedico(),
                                    cedula != null && !cedula.isBlank() ? cedula : "—");
                        });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void actualizarBienvenida() {
        if (lblBienvenida != null && pacienteActual != null) {
            String nombre = pacienteActual.getNombre() != null ? pacienteActual.getNombre() : "";
            lblBienvenida.setText("Hola, " + nombre + " " +
                    (pacienteActual.getApellido() != null ? pacienteActual.getApellido() : ""));
        }
    }

    // ── Configuración de columnas ─────────────────────────────────────────────

    private void configurarColumnas(TableView<CitaDTO> tabla,
            TableColumn<CitaDTO, String> colNomPac, TableColumn<CitaDTO, String> colCedMed,
            TableColumn<CitaDTO, String> colNomMed, TableColumn<CitaDTO, String> colFecha,
            TableColumn<CitaDTO, String> colHoraIni, TableColumn<CitaDTO, String> colHoraFin,
            TableColumn<CitaDTO, Void> colEstado) {

        // Nombre del paciente
        colNomPac.setCellValueFactory(c -> {
            if (pacienteActual != null) {
                String nombre = (pacienteActual.getNombre() != null ? pacienteActual.getNombre() : "")
                        + " " + (pacienteActual.getApellido() != null ? pacienteActual.getApellido() : "");
                return new SimpleStringProperty(nombre.trim());
            }
            return new SimpleStringProperty("Paciente #" + c.getValue().getIdPaciente());
        });

        // Cédula del médico — obtenida cruzando MedicoDTO.idUsuario con PersonaDTO.cedulaCiudadania
        colCedMed.setCellValueFactory(c -> {
            String cedula = mapaCedulasMedicos.get(c.getValue().getIdMedico());
            return new SimpleStringProperty(cedula != null ? cedula : "—");
        });

        // Nombre del médico
        colNomMed.setCellValueFactory(c -> {
            MedicoDTO m = mapaMedicos.get(c.getValue().getIdMedico());
            if (m != null) {
                String nombre = (m.getNombre() != null ? m.getNombre() : "")
                        + " " + (m.getApellido() != null ? m.getApellido() : "");
                return new SimpleStringProperty(nombre.trim());
            }
            return new SimpleStringProperty("Médico #" + c.getValue().getIdMedico());
        });

        // Fecha
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFecha() != null ? c.getValue().getFecha().toString() : ""));

        // Hora inicio
        colHoraIni.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getHoraInicio() != null ? c.getValue().getHoraInicio().toString() : ""));

        // Hora fin
        colHoraFin.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getHoraFin() != null ? c.getValue().getHoraFin().toString() : ""));

        // Estado — badge visual con color (igual que en ControladorMedico y ControladorAgendador)
        colEstado.setCellFactory(param -> new TableCell<>() {
            private final Label lblEstado = new Label();

            {
                lblEstado.setMinWidth(110);
                lblEstado.setPrefWidth(130);
                lblEstado.setWrapText(false);
                lblEstado.setAlignment(Pos.CENTER);
                lblEstado.setStyle("-fx-alignment: center; -fx-padding: 6px 14px; -fx-background-radius: 6;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView() == null || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }
                CitaDTO cita = getTableView().getItems().get(getIndex());
                if (cita == null) { setGraphic(null); return; }

                Integer idEstado = cita.getIdEstadoCita();
                lblEstado.setText(EstadoCita.getIcono(idEstado) + " " + EstadoCita.getNombre(idEstado));
                lblEstado.setStyle(EstadoCita.generarEstiloCSS(idEstado));
                lblEstado.setTooltip(new Tooltip(EstadoCita.getDescripcion(idEstado)));
                setGraphic(lblEstado);
            }
        });
    }

    // ── Carga de tablas ───────────────────────────────────────────────────────

    private void cargarTablas() {
        if (idPaciente <= 0) return;
        try {
            // Traer todas las citas del paciente y clasificar en el frontend
            // para garantizar que estados terminados (Completada/Cancelada/No Asistió)
            // vayan siempre a Historial, independientemente de la fecha.
            List<CitaDTO> todasLasCitas = backend.listarTodasLasCitasPaciente(idPaciente);

            List<CitaDTO> historial = todasLasCitas.stream()
                    .filter(c -> esHistorial(c))
                    .toList();

            List<CitaDTO> futuras = todasLasCitas.stream()
                    .filter(c -> !esHistorial(c))
                    .toList();

            tblHistorial.setItems(FXCollections.observableArrayList(historial));
            tblFuturas.setItems(FXCollections.observableArrayList(futuras));
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Una cita va a Historial si:
     *  - Su estado es Completada, Cancelada o No Asistió (independiente de la fecha), O
     *  - Su fecha es pasada (ya ocurrió)
     */
    private boolean esHistorial(CitaDTO cita) {
        Integer estado = cita.getIdEstadoCita();
        if (estado != null && ESTADOS_HISTORIAL.contains(estado)) return true;
        return cita.getFecha() != null && cita.getFecha().isBefore(java.time.LocalDate.now());
    }

    @FXML
    void onAgendarCita(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/presentacion/vistas/VistaAgendarCita.fxml"));
            Parent root = loader.load();
            ControladorAgendarCita ctrl = loader.getController();
            ctrl.setModoPaciente(idPaciente);
            Stage stage = (Stage) tblHistorial.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @FXML
    void onCerrarSesion(ActionEvent e) {
        SesionUsuario.getInstancia().limpiarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/presentacion/vistas/VistaLogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tblHistorial.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
