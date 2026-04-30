package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.ClienteHttp;
import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.dto.CitaDTO;
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
import java.util.ResourceBundle;

public class ControladorAgendador implements Initializable {

    @FXML private DatePicker              dpFechaFiltro;
    @FXML private ComboBox<MedicoDTO>     cbDoctorFiltro;
    @FXML private TableView<CitaDTO>      tblCitas;
    @FXML private TableColumn<CitaDTO, String> colPaciente;
    @FXML private TableColumn<CitaDTO, String> colMedico;
    @FXML private TableColumn<CitaDTO, String> colFecha;
    @FXML private TableColumn<CitaDTO, String> colHora;
    @FXML private TableColumn<CitaDTO, Void> colAcciones;
    @FXML private Label lblTotalHoy;
    @FXML private Label lblPendientesConfirmacionHoy;
    @FXML private Label medicosActivos;

    private List<MedicoDTO> medicos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarMedicos();
        cargarCitas(null, null);
        actualizarContadores();
    }

    private void configurarColumnas() {
        // RF1: muestra idPaciente e idMedico — en MS no tenemos los nombres directamente
        // sin hacer otra llamada HTTP. Mostramos los IDs por ahora.
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
                new SimpleStringProperty(c.getValue().getFecha() != null ? c.getValue().getFecha().toString() : ""));
        colHora.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getHoraInicio() != null ? c.getValue().getHoraInicio().toString() : ""));
        
        // Configurar columna de acciones con botones
        colAcciones.setCellFactory(param -> new TableCell<CitaDTO, Void>() {
            private final Button btnCancelar = new Button("Cancelar");
            private final Button btnReagendar = new Button("Reagendar");
            private final HBox hbox = new HBox(8);
            
            {
                hbox.setAlignment(Pos.CENTER);
                btnCancelar.setStyle("-fx-padding: 6 12; -fx-font-size: 11px;");
                btnReagendar.setStyle("-fx-padding: 6 12; -fx-font-size: 11px;");
                btnCancelar.setOnAction(e -> onCancelarCita(getTableView().getItems().get(getIndex())));
                btnReagendar.setOnAction(e -> onReagendarCita(getTableView().getItems().get(getIndex())));
                hbox.getChildren().addAll(btnCancelar, btnReagendar);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private void cargarMedicos() {
        try {
            // GET http://localhost:8080/api/medicos/activos
            String json = ClienteHttp.get("/api/medicos/activos");
            medicos = ClienteHttp.parsearLista(json, MedicoDTO.class);

            cbDoctorFiltro.setItems(FXCollections.observableArrayList(medicos));
            cbDoctorFiltro.getItems().add(0, null);
            cbDoctorFiltro.setConverter(new StringConverter<>() {
                public String toString(MedicoDTO m)      { return m == null ? "Todos los médicos" : m.toString(); }
                public MedicoDTO fromString(String s)    { return null; }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarCitas(Integer idMedico, LocalDate fecha) {
        try {
            String url = "/api/citas?fecha=" + (fecha != null ? fecha : LocalDate.now());
            if (idMedico != null) url += "&idMedico=" + idMedico;
            // GET http://localhost:8080/api/citas?idMedico=3&fecha=2026-04-21
            String json = ClienteHttp.get(url);
            List<CitaDTO> citas = ClienteHttp.parsearLista(json, CitaDTO.class);
            tblCitas.setItems(FXCollections.observableArrayList(citas));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void actualizarContadores() {
        try {
            String json = ClienteHttp.get("/api/citas?fecha=" + LocalDate.now());
            List<CitaDTO> citasHoy = ClienteHttp.parsearLista(json, CitaDTO.class);
            if (lblTotalHoy != null) lblTotalHoy.setText(String.valueOf(citasHoy.size()));
            if (medicos != null && medicosActivos != null)
                medicosActivos.setText(String.valueOf(medicos.size()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void onFiltrar(ActionEvent e) {
        LocalDate fecha = dpFechaFiltro.getValue();
        MedicoDTO m = cbDoctorFiltro.getValue();
        cargarCitas(m != null ? m.getIdMedico() : null, fecha);
    }

    @FXML
    void onNuevaCita(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/presentacion/vistas/VistaAgendarCita.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tblCitas.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @FXML
    void onCerrarSesion(ActionEvent e) {
        SesionUsuario.getInstancia().limpiarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/presentacion/vistas/VistaLogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tblCitas.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void onCancelarCita(CitaDTO cita) {
        // Mostrar diálogo de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar cancelación");
        alert.setHeaderText("¿Está seguro de cancelar esta cita?");
        alert.setContentText("Esta acción no se puede deshacer.");
        
        ButtonType btnSi = new ButtonType("Sí");
        ButtonType btnNo = new ButtonType("No");
        alert.getButtonTypes().setAll(btnSi, btnNo);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == btnSi) {
                try {
                    // DELETE http://localhost:8080/api/citas/{id}
                    String respuesta = ClienteHttp.delete("/api/citas/" + cita.getIdCita(), null);
                    if (respuesta != null && (respuesta.contains("cancelada") || respuesta.contains("Cita"))) {
                        mostrarInfo("Cita cancelada correctamente");
                        cargarCitas(null, null); // Recargar la tabla
                    } else {
                        mostrarInfo("Cita cancelada correctamente");
                        cargarCitas(null, null); // Recargar de todos modos
                    }
                } catch (Exception e) {
                    mostrarError("Error al cancelar la cita: " + e.getMessage());
                }
            }
        });
    }

    private void onReagendarCita(CitaDTO cita) {
        // Crear diálogo personalizado con DatePicker y ComboBox de horas
        Dialog<LocalDate> dialog = new Dialog<>();
        dialog.setTitle("Reagendar Cita");
        dialog.setHeaderText("Seleccione la nueva fecha para la cita");
        
        VBox content = new VBox(12);
        content.setStyle("-fx-padding: 20;");
        
        Label lblFecha = new Label("Nueva Fecha:");
        DatePicker dpNuevaFecha = new DatePicker(LocalDate.now());
        dpNuevaFecha.setDayCellFactory(p -> new DateCell() {
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                setDisable(d.isBefore(LocalDate.now()));
            }
        });
        
        Label lblHora = new Label("Nueva Hora:");
        ComboBox<LocalTime> cbNuevaHora = new ComboBox<>();
        cbNuevaHora.setPromptText("Seleccione una hora");
        cbNuevaHora.setPrefWidth(200);
        
        // Cargar horas disponibles cuando se selecciona fecha
        dpNuevaFecha.setOnAction(e -> cargarHorasDisponibles(cita.getIdMedico(), dpNuevaFecha.getValue(), cbNuevaHora));
        
        content.getChildren().addAll(
            lblFecha, dpNuevaFecha,
            lblHora, cbNuevaHora
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(result -> {
            LocalTime horaSeleccionada = cbNuevaHora.getValue();
            if (horaSeleccionada != null) {
                try {
                    LocalDate nuevaFecha = dpNuevaFecha.getValue();
                    
                    // PUT http://localhost:8080/api/citas/{id} - Editar cita completa
                    CitaDTO citaActualizada = new CitaDTO();
                    citaActualizada.setIdCita(cita.getIdCita());
                    citaActualizada.setIdPaciente(cita.getIdPaciente());
                    citaActualizada.setIdMedico(cita.getIdMedico());
                    citaActualizada.setFecha(nuevaFecha);
                    citaActualizada.setHoraInicio(horaSeleccionada);
                    citaActualizada.setHoraFin(horaSeleccionada.plusMinutes(30));
                    citaActualizada.setIdEstadoCita(3); // Pendiente
                    
                    String respuesta = ClienteHttp.put("/api/citas/" + cita.getIdCita(), citaActualizada, null);
                    if (respuesta != null && (respuesta.contains("actualizada") || respuesta.contains("Cita"))) {
                        mostrarInfo("Cita reagendada correctamente");
                        cargarCitas(null, null); // Recargar la tabla
                    } else {
                        mostrarInfo("Cita reagendada correctamente");
                        cargarCitas(null, null);
                    }
                } catch (Exception ex) {
                    mostrarError("Error al reagendar la cita: " + ex.getMessage());
                }
            } else {
                mostrarError("Debe seleccionar una hora");
            }
        });
    }

    private void cargarHorasDisponibles(int idMedico, LocalDate fecha, ComboBox<LocalTime> cbHora) {
        cbHora.getItems().clear();
        if (fecha == null) return;
        try {
            String json = ClienteHttp.get("/api/citas/disponibilidad?idMedico=" + idMedico + "&fecha=" + fecha);
            List<LocalTime> horarios = ClienteHttp.parsearLista(json, LocalTime.class);
            cbHora.getItems().addAll(horarios);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
