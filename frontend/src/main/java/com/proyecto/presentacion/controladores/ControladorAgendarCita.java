package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.ClienteHttp;
import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.dto.CitaDTO;
import com.proyecto.presentacion.dto.MedicoDTO;
import com.proyecto.presentacion.dto.PersonaDTO;
import com.proyecto.presentacion.facade.BackendFacade;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class ControladorAgendarCita {

    @FXML private TextField          txtNombre;
    @FXML private TextField          txtApellido;
    @FXML private TextField          txtCedula;
    @FXML private TextField          txtCorreo;
    @FXML private TextField          txtCelular;
    @FXML private ChoiceBox<String>  cbGenero;
    @FXML private ComboBox<MedicoDTO> cbMedico;
    @FXML private DatePicker         dpFecha;
    @FXML private DatePicker         dpFechaNac;
    @FXML private ComboBox<LocalTime> cbHora;
    @FXML private TextArea           txtMotivo;
    @FXML private Button             btnGuardar;
    @FXML private Label              lblErrorCedula;
    @FXML private Label              lblErrorNombre;
    @FXML private Label              lblErrorApellido;
    @FXML private Label              lblErrorCorreo;
    @FXML private Label              lblErrorCelular;
    @FXML private Label              lblErrorMotivo;
    private final BackendFacade backendFacade = new BackendFacade();

    @FXML
    public void initialize() {
        cargarMedicos();
        cargarGenero();
        cbMedico.setOnAction(e -> actualizarHorarios());
        dpFecha.setOnAction(e -> actualizarHorarios());
        iniciarValidaciones();
    }

    private void cargarGenero() {
        cbGenero.getItems().addAll("Masculino", "Femenino", "Otro");
    }

    private void cargarMedicos() {
        try {
            List<MedicoDTO> lista = backendFacade.listarMedicosActivos();
            cbMedico.getItems().addAll(lista);
            cbMedico.setCellFactory(p -> new ListCell<>() {
                protected void updateItem(MedicoDTO m, boolean empty) {
                    super.updateItem(m, empty);
                    setText(empty || m == null ? "" : m.toString());
                }
            });
            cbMedico.setButtonCell(new ListCell<>() {
                protected void updateItem(MedicoDTO m, boolean empty) {
                    super.updateItem(m, empty);
                    setText(empty || m == null ? "" : m.toString());
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void actualizarHorarios() {
        cbHora.getItems().clear();
        if (cbMedico.getValue() == null || dpFecha.getValue() == null) return;
        try {
            // GET http://localhost:8080/api/citas/disponibilidad?idMedico=3&fecha=2026-04-28
            String json = ClienteHttp.get("/api/citas/disponibilidad?idMedico="
                    + cbMedico.getValue().getIdMedico() + "&fecha=" + dpFecha.getValue());
            List<LocalTime> horarios = ClienteHttp.parsearLista(json, LocalTime.class);
            cbHora.getItems().addAll(horarios);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void onBuscarPaciente() {
        String cedula = txtCedula.getText();
        if (cedula == null || cedula.isEmpty()) { mostrarError("Ingrese una cédula para buscar"); return; }
        try {
            PersonaDTO persona = backendFacade.buscarPacientePorDocumento(cedula);
            if (persona.getIdPersona() == 0) { mostrarError("No se encontró ninguna persona con esa cédula"); return; }
            precargarPersona(persona);
            mostrarInfo("Paciente cargado correctamente");
        } catch (Exception e) { mostrarError("No se encontró ninguna persona con esa cédula"); }
    }

    private void precargarPersona(PersonaDTO p) {
        txtNombre.setText(p.getNombre());
        txtApellido.setText(p.getApellido());
        if (p.getCorreo() != null) txtCorreo.setText(p.getCorreo());
        if (p.getCelular() != null) txtCelular.setText(p.getCelular());
        if (p.getFechaNacimiento() != null) dpFechaNac.setValue(p.getFechaNacimiento());
        if (p.getIdGenero() != null) cbGenero.setValue(switch (p.getIdGenero()) {
            case 1 -> "Masculino"; case 2 -> "Femenino"; default -> "Otro";
        });
        txtNombre.setDisable(true); txtApellido.setDisable(true);
        txtCorreo.setDisable(true); txtCelular.setDisable(true);
        dpFechaNac.setDisable(true); cbGenero.setDisable(true);
    }

    @FXML
    private void onGuardarCita() {
        if (!validar()) return;
        try {
            // Buscar si el paciente ya existe
            PersonaDTO persona = backendFacade.buscarPacientePorDocumento(txtCedula.getText());

            int idPaciente;
            if (persona.getIdPersona() > 0) {
                idPaciente = persona.getIdPersona();
            } else {
                // RF2: registrar paciente nuevo
                // POST http://localhost:8080/api/pacientes
                PersonaDTO nuevo = backendFacade.registrarPaciente(Map.of(
                        "paciente", Map.of(
                                "nombre", txtNombre.getText(),
                                "apellido", txtApellido.getText(),
                                "cedulaCiudadania", txtCedula.getText(),
                                "celular", txtCelular.getText(),
                                "correo", txtCorreo.getText(),
                                "idGenero", generoAId(cbGenero.getValue()),
                                "idEstado", 2
                        ),
                        "usuario", Map.of(
                                "usuario", txtCedula.getText(),
                                "contrasena", txtCedula.getText()
                        )
                ));
                idPaciente = nuevo.getIdPersona();
            }

            // RF2/RF3: agendar cita
            // POST http://localhost:8080/api/citas/web
            String respuesta = backendFacade.agendarCitaWeb(
                    idPaciente,
                    cbMedico.getValue().getIdMedico(),
                    dpFecha.getValue(),
                    cbHora.getValue()
            );

            if (respuesta.contains("agendada")) {
                mostrarInfo("Cita agendada correctamente");
                limpiar();
            } else {
                mostrarError("Ese horario ya no está disponible");
            }
        } catch (Exception e) { mostrarError("Error al agendar: " + e.getMessage()); }
    }

    private int generoAId(String genero) {
        if (genero == null) return 4;
        return switch (genero) { case "Masculino" -> 1; case "Femenino" -> 2; default -> 3; };
    }

    private boolean validar() {
        if (txtNombre.getText().isEmpty() || txtApellido.getText().isEmpty()
                || txtCedula.getText().isEmpty() || cbMedico.getValue() == null
                || dpFecha.getValue() == null || cbHora.getValue() == null) {
            mostrarError("Completa todos los campos obligatorios");
            return false;
        }
        return true;
    }

    private void limpiar() {
        txtNombre.clear(); txtApellido.clear(); txtCedula.clear();
        txtCorreo.clear(); txtCelular.clear(); txtMotivo.clear();
        cbMedico.setValue(null); cbHora.getItems().clear();
        dpFecha.setValue(null); cbGenero.setValue(null);
        txtNombre.setDisable(false); txtApellido.setDisable(false);
        txtCorreo.setDisable(false); txtCelular.setDisable(false);
        dpFechaNac.setDisable(false); cbGenero.setDisable(false);
    }

    /**
     * Llamado desde ControladorPaciente — precarga datos del paciente logueado.
     */
    public void setModoPaciente(int idPaciente) {
        if (idPaciente <= 0) return;
        try {
            String json = ClienteHttp.get("/api/pacientes/" + idPaciente);
            PersonaDTO p = ClienteHttp.parsear(json, PersonaDTO.class);
            txtCedula.setText(p.getCedulaCiudadania());
            precargarPersona(p);
            txtCedula.setDisable(true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getRol();
        String vista = (rol != null && rol.equals("paciente"))
                ? "/com/presentacion/vistas/VistaPaciente.fxml"
                : "/com/presentacion/vistas/VistaAgendador.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));
            Parent root = loader.load();
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void iniciarValidaciones() {
        dpFecha.setDayCellFactory(p -> new DateCell() {
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty); setDisable(d.isBefore(LocalDate.now()));
            }
        });
        dpFechaNac.setDayCellFactory(p -> new DateCell() {
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty); setDisable(d.isAfter(LocalDate.now()));
            }
        });
    }

    private void mostrarError(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void mostrarInfo(String msg)  { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
}
