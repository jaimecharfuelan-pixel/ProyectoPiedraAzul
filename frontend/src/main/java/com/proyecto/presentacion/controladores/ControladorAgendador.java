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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
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
}
