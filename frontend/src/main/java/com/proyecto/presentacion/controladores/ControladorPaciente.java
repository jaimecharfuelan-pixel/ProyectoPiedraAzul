package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.ClienteHttp;
import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.dto.CitaDTO;
import com.proyecto.presentacion.dto.PersonaDTO;

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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ControladorPaciente implements Initializable {

    @FXML private TableView<CitaDTO>           tblHistorial;
    @FXML private TableColumn<CitaDTO, String> colHistNomPaciente;
    @FXML private TableColumn<CitaDTO, String> colHistCedMedico;
    @FXML private TableColumn<CitaDTO, String> colHistNomMedico;
    @FXML private TableColumn<CitaDTO, String> colHistFecha;
    @FXML private TableColumn<CitaDTO, String> colHistHoraInicio;
    @FXML private TableColumn<CitaDTO, String> colHistHoraFin;
    @FXML private TableColumn<CitaDTO, String> colHistEstado;

    @FXML private TableView<CitaDTO>           tblFuturas;
    @FXML private TableColumn<CitaDTO, String> colFutNomPaciente;
    @FXML private TableColumn<CitaDTO, String> colFutCedMedico;
    @FXML private TableColumn<CitaDTO, String> colFutNomMedico;
    @FXML private TableColumn<CitaDTO, String> colFutFecha;
    @FXML private TableColumn<CitaDTO, String> colFutHoraInicio;
    @FXML private TableColumn<CitaDTO, String> colFutHoraFin;
    @FXML private TableColumn<CitaDTO, String> colFutEstado;

    private int idPaciente;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idPaciente = resolverIdPaciente();
        configurarColumnas(tblHistorial, colHistNomPaciente, colHistCedMedico,
                colHistNomMedico, colHistFecha, colHistHoraInicio, colHistHoraFin, colHistEstado);
        configurarColumnas(tblFuturas, colFutNomPaciente, colFutCedMedico,
                colFutNomMedico, colFutFecha, colFutHoraInicio, colFutHoraFin, colFutEstado);
        cargarTablas();
    }

    private int resolverIdPaciente() {
        int idUsuario = SesionUsuario.getInstancia().getIdUsuario();
        if (idUsuario <= 0) return -1;
        try {
            // GET http://localhost:8080/api/personas
            String json = ClienteHttp.get("/api/personas");
            List<PersonaDTO> personas = ClienteHttp.parsearLista(json, PersonaDTO.class);
            PersonaDTO persona = personas.stream()
                    .filter(p -> p.getIdUsuario() != null && p.getIdUsuario() == idUsuario)
                    .findFirst().orElse(null);
            if (persona == null) return -1;
            SesionUsuario.getInstancia().setIdPacienteActual(persona.getIdPersona());
            return persona.getIdPersona();
        } catch (Exception e) { e.printStackTrace(); return -1; }
    }

    private void configurarColumnas(TableView<CitaDTO> tabla,
            TableColumn<CitaDTO, String> colNomPac, TableColumn<CitaDTO, String> colCedMed,
            TableColumn<CitaDTO, String> colNomMed, TableColumn<CitaDTO, String> colFecha,
            TableColumn<CitaDTO, String> colHoraIni, TableColumn<CitaDTO, String> colHoraFin,
            TableColumn<CitaDTO, String> colEstado) {

        colNomPac.setCellValueFactory(c -> new SimpleStringProperty("Paciente #" + c.getValue().getIdPaciente()));
        colCedMed.setCellValueFactory(c -> new SimpleStringProperty("Médico #" + c.getValue().getIdMedico()));
        colNomMed.setCellValueFactory(c -> new SimpleStringProperty("Médico #" + c.getValue().getIdMedico()));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFecha() != null ? c.getValue().getFecha().toString() : ""));
        colHoraIni.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getHoraInicio() != null ? c.getValue().getHoraInicio().toString() : ""));
        colHoraFin.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getHoraFin() != null ? c.getValue().getHoraFin().toString() : ""));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getIdEstadoCita() != null ? String.valueOf(c.getValue().getIdEstadoCita()) : ""));
    }

    private void cargarTablas() {
        if (idPaciente <= 0) return;
        try {
            // GET http://localhost:8080/api/citas/paciente/{id}/historial
            String jsonHist = ClienteHttp.get("/api/citas/paciente/" + idPaciente + "/historial");
            List<CitaDTO> historial = ClienteHttp.parsearLista(jsonHist, CitaDTO.class);
            tblHistorial.setItems(FXCollections.observableArrayList(historial));

            // GET http://localhost:8080/api/citas/paciente/{id}/futuras
            String jsonFut = ClienteHttp.get("/api/citas/paciente/" + idPaciente + "/futuras");
            List<CitaDTO> futuras = ClienteHttp.parsearLista(jsonFut, CitaDTO.class);
            tblFuturas.setItems(FXCollections.observableArrayList(futuras));
        } catch (Exception e) { e.printStackTrace(); }
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
