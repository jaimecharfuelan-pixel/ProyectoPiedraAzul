package com.proyecto.presentacion.controladores;

import com.proyecto.logica.modelos.Cita;
import com.proyecto.logica.modelos.Paciente;
import com.proyecto.logica.servicios.ServicioPaciente;
import com.proyecto.persistencia.repositorios.RepositorioCitas;
import com.proyecto.persistencia.repositorios.RepositorioPaciente;
import com.proyecto.persistencia.repositorios.RepositorioPersona;
import com.proyecto.persistencia.repositorios.RepositorioUsuario;
import com.proyecto.presentacion.SesionUsuario;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ControladorPaciente implements Initializable {

    // ── Tabla historial (citas pasadas) ───────────────────────
    @FXML private TableView<Cita>           tblHistorial;
    @FXML private TableColumn<Cita, String> colHistNomPaciente;
    @FXML private TableColumn<Cita, String> colHistCedMedico;
    @FXML private TableColumn<Cita, String> colHistNomMedico;
    @FXML private TableColumn<Cita, String> colHistFecha;
    @FXML private TableColumn<Cita, String> colHistHoraInicio;
    @FXML private TableColumn<Cita, String> colHistHoraFin;
    @FXML private TableColumn<Cita, String> colHistEstado;

    // ── Tabla citas futuras ───────────────────────────────────
    @FXML private TableView<Cita>           tblFuturas;
    @FXML private TableColumn<Cita, String> colFutNomPaciente;
    @FXML private TableColumn<Cita, String> colFutCedMedico;
    @FXML private TableColumn<Cita, String> colFutNomMedico;
    @FXML private TableColumn<Cita, String> colFutFecha;
    @FXML private TableColumn<Cita, String> colFutHoraInicio;
    @FXML private TableColumn<Cita, String> colFutHoraFin;
    @FXML private TableColumn<Cita, String> colFutEstado;

    private ServicioPaciente servicioPaciente;
    private RepositorioPersona repoPersona;
    private int idPaciente;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        repoPersona      = new RepositorioPersona();
        servicioPaciente = new ServicioPaciente(new RepositorioUsuario(), new RepositorioCitas());

        // Obtener el id_persona del paciente logueado desde la sesión
        idPaciente = resolverIdPaciente();

        configurarColumnas(tblHistorial,
                colHistNomPaciente, colHistCedMedico, colHistNomMedico,
                colHistFecha, colHistHoraInicio, colHistHoraFin, colHistEstado);

        configurarColumnas(tblFuturas,
                colFutNomPaciente, colFutCedMedico, colFutNomMedico,
                colFutFecha, colFutHoraInicio, colFutHoraFin, colFutEstado);

        cargarTablas();
    }

    /**
     * Busca el id_persona del paciente logueado usando el id_usuario guardado en sesión.
     */
    private int resolverIdPaciente() {
        int idUsuario = SesionUsuario.getInstancia().getIdUsuario();
        if (idUsuario <= 0) return -1;
        var persona = repoPersona.listar().stream()
            .filter(p -> p.getIdUsuario() != null && p.getIdUsuario() == idUsuario)
            .findFirst().orElse(null);
        if (persona == null) return -1;
        // Guardar en sesión para que ControladorAgendarCita lo use
        SesionUsuario.getInstancia().setIdPacienteActual(persona.getIdPersona());
        return persona.getIdPersona();
    }

    private void configurarColumnas(
            TableView<Cita> tabla,
            TableColumn<Cita, String> colNomPac,
            TableColumn<Cita, String> colCedMed,
            TableColumn<Cita, String> colNomMed,
            TableColumn<Cita, String> colFecha,
            TableColumn<Cita, String> colHoraIni,
            TableColumn<Cita, String> colHoraFin,
            TableColumn<Cita, String> colEstado) {

        colNomPac.setCellValueFactory(c -> {
            var p = repoPersona.buscarPorId(c.getValue().getIdPaciente());
            return new SimpleStringProperty(p != null ? p.getNombre() + " " + p.getApellido() : "");
        });
        colCedMed.setCellValueFactory(c -> {
            var p = repoPersona.buscarPorId(c.getValue().getIdMedico());
            return new SimpleStringProperty(p != null ? p.getCedulaCiudadania() : "");
        });
        colNomMed.setCellValueFactory(c -> {
            var p = repoPersona.buscarPorId(c.getValue().getIdMedico());
            return new SimpleStringProperty(p != null ? p.getNombre() + " " + p.getApellido() : "");
        });
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

        List<Cita> historial = servicioPaciente.obtenerHistorialCitas(idPaciente);
        List<Cita> futuras   = servicioPaciente.obtenerCitasFuturas(idPaciente);

        tblHistorial.setItems(FXCollections.observableArrayList(historial));
        tblFuturas.setItems(FXCollections.observableArrayList(futuras));
    }

    @FXML
    void onAgendarCita(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/presentacion/vistas/VistaAgendarCita.fxml"));
            Parent root = loader.load();
            // El controlador ya fue cargado; le indicamos que es modo paciente
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
