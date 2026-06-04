package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.facade.BackendFacade;
import com.proyecto.presentacion.util.Conversiones;
import com.proyecto.presentacion.dto.PersonaDTO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ControladorRegistroPaciente {

    @FXML private TextField           txtNombre;
    @FXML private TextField           txtApellido;
    @FXML private TextField           txtCedula;
    @FXML private TextField           txtCelular;
    @FXML private TextField           txtCorreo;
    @FXML private ChoiceBox<String>   cbGenero;
    @FXML private DatePicker          dpFechaNac;
    @FXML private TextField           txtUsuario;
    @FXML private PasswordField       txtContrasena;
    @FXML private PasswordField       txtConfirmarContrasena;

    @FXML private Label               lblErrorNombre;
    @FXML private Label               lblErrorApellido;
    @FXML private Label               lblErrorCedula;
    @FXML private Label               lblErrorCelular;
    @FXML private Label               lblErrorCorreo;
    @FXML private Label               lblErrorGenero;
    @FXML private Label               lblErrorFechaNac;
    @FXML private Label               lblErrorUsuario;
    @FXML private Label               lblErrorContrasena;
    @FXML private Label               lblErrorConfirmarContrasena;

    @FXML private VBox                panelDatosPaciente;
    @FXML private VBox                panelDatosUsuario;
    @FXML private Button              btnSiguiente;
    @FXML private Button              btnGuardar;
    @FXML private Button              btnCancelar;

    private final BackendFacade backendFacade = new BackendFacade();

    @FXML
    public void initialize() {
        cargarGeneros();
        iniciarListeners();
        iniciarValidacionesFecha();
    }

    // ─── Carga de datos ───────────────────────────────────────────────────────

    private void cargarGeneros() {
        cbGenero.getItems().addAll("Masculino", "Femenino", "No Binario", "Prefiero no decir");
    }

    // ─── Listeners de validación en tiempo real ───────────────────────────────

    private void iniciarListeners() {
        // Nombre: texto, máx 100, sin caracteres especiales
        txtNombre.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.length() > 100) { txtNombre.setText(old); return; }
            aplicarEstilo(txtNombre, lblErrorNombre, validarTexto(nuevo, "Nombre"));
        });

        // Apellido: texto, máx 100, sin caracteres especiales
        txtApellido.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.length() > 100) { txtApellido.setText(old); return; }
            aplicarEstilo(txtApellido, lblErrorApellido, validarTexto(nuevo, "Apellido"));
        });

        // Cédula: solo números, máx 20
        txtCedula.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.length() > 20) { txtCedula.setText(old); return; }
            if (!nuevo.isEmpty() && !nuevo.matches("[0-9]*")) { txtCedula.setText(old); return; }
            aplicarEstilo(txtCedula, lblErrorCedula, validarNumerico(nuevo, "Cédula", 20));
        });

        // Celular: solo números, máx 15
        txtCelular.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.length() > 15) { txtCelular.setText(old); return; }
            if (!nuevo.isEmpty() && !nuevo.matches("[0-9]*")) { txtCelular.setText(old); return; }
            aplicarEstilo(txtCelular, lblErrorCelular, validarNumerico(nuevo, "Celular", 15));
        });

        // Correo
        txtCorreo.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.contains(" ")) { txtCorreo.setText(old); return; }
            aplicarEstilo(txtCorreo, lblErrorCorreo, validarCorreo(nuevo));
        });

        // Usuario: máx 50
        txtUsuario.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.length() > 50) { txtUsuario.setText(old); return; }
            aplicarEstilo(txtUsuario, lblErrorUsuario, validarUsuario(nuevo));
        });

        // Contraseña
        txtContrasena.textProperty().addListener((obs, old, nuevo) -> {
            aplicarEstilo(txtContrasena, lblErrorContrasena, validarContrasena(nuevo));
        });

        // Confirmar Contraseña
        txtConfirmarContrasena.textProperty().addListener((obs, old, nuevo) -> {
            aplicarEstilo(txtConfirmarContrasena, lblErrorConfirmarContrasena, validarConfirmarContrasena());
        });
    }

    // ─── Reglas de validación ─────────────────────────────────────────────────

    private String validarTexto(String valor, String campo) {
        if (valor.isEmpty()) return "";
        if (valor.startsWith(" "))          return campo + " no puede iniciar con espacio";
        if (valor.contains("  "))           return "No se permiten espacios dobles";
        if (!valor.matches("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]*"))
            return "Solo se permiten letras en " + campo;
        return "";
    }

    private String validarNumerico(String valor, String campo, int max) {
        if (valor.isEmpty()) return "";
        if (valor.startsWith(" "))  return campo + " no puede iniciar con espacio";
        if (valor.contains("  "))   return "No se permiten espacios dobles";
        return "";
    }

    private String validarCorreo(String valor) {
        if (valor.isEmpty()) return "";
        if (valor.startsWith(" "))  return "Correo no puede iniciar con espacio";
        if (valor.contains("  "))   return "No se permiten espacios dobles";
        if (valor.length() > 100)   return "Correo demasiado largo";
        if (!valor.matches("^[A-Za-z0-9+_.-]*@[A-Za-z0-9.-]*$") && !valor.isEmpty())
            return "Correo no válido";
        return "";
    }

    private String validarUsuario(String valor) {
        if (valor.isEmpty()) return "";
        if (valor.startsWith(" "))  return "Usuario no puede iniciar con espacio";
        if (valor.contains("  "))   return "No se permiten espacios dobles";
        if (valor.length() < 3)     return "Usuario debe tener al menos 3 caracteres";
        return "";
    }

    private String validarContrasena(String valor) {
        if (valor.isEmpty()) return "";
        if (valor.length() < 4)     return "Contraseña debe tener al menos 4 caracteres";
        if (valor.length() > 50)    return "Contraseña demasiado larga";
        return "";
    }

    private String validarConfirmarContrasena() {
        String cont = txtContrasena.getText();
        String confirmar = txtConfirmarContrasena.getText();
        if (confirmar.isEmpty()) return "";
        if (!cont.equals(confirmar)) return "Las contraseñas no coinciden";
        return "";
    }

    private void aplicarEstilo(Control campo, Label lblError, String error) {
        if (lblError == null) return;
        if (error == null || error.isEmpty()) {
            campo.setStyle("-fx-border-color: #43a047; -fx-border-width: 1.5; -fx-border-radius: 8;");
            lblError.setText("");
        } else {
            campo.setStyle("-fx-border-color: #e53935; -fx-border-width: 1.5; -fx-border-radius: 8;");
            lblError.setText(error);
        }
    }

    // ─── Inicialización de fechas ──────────────────────────────────────────────

    private void iniciarValidacionesFecha() {
        dpFechaNac.setDayCellFactory(p -> new DateCell() {
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                setDisable(d.isAfter(LocalDate.now()));
            }
        });
    }

    // ─── Acciones ─────────────────────────────────────────────────────────────

    @FXML
    private void onSiguiente() {
        if (!validarPanel1()) return;

        // Cambiar visibilidad de paneles
        panelDatosPaciente.setVisible(false);
        panelDatosPaciente.setManaged(false);
        panelDatosUsuario.setVisible(true);
        panelDatosUsuario.setManaged(true);

        // Cambiar botones
        btnSiguiente.setVisible(false);
        btnSiguiente.setManaged(false);
        btnGuardar.setVisible(true);
        btnGuardar.setManaged(true);

        // Enfoque en el primer campo del panel 2
        txtUsuario.requestFocus();
    }

    @FXML
    private void onGuardar() {
        if (!validarPanel1() || !validarPanel2()) return;

        try {
            // Construir el body plano que espera RegistrarPacienteCommand
            Map<String, Object> body = new HashMap<>();
            body.put("nombre",           txtNombre.getText().trim());
            body.put("apellido",         txtApellido.getText().trim());
            body.put("cedulaCiudadania", txtCedula.getText().trim());
            body.put("celular",          txtCelular.getText().trim());
            body.put("correo",           txtCorreo.getText().trim());
            body.put("idGenero",         generoAId(cbGenero.getValue()));
            body.put("fechaNacimiento",  dpFechaNac.getValue());
            body.put("usuarioLogin",     txtUsuario.getText().trim());
            body.put("contrasena",       txtContrasena.getText());

            PersonaDTO nuevo = backendFacade.registrarPaciente(body);
            mostrarInfo("¡Paciente registrado correctamente!\n\nUsuario: " + txtUsuario.getText());
            irALogin();
        } catch (Exception e) {
            mostrarError("Error al registrar: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/presentacion/vistas/VistaLogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            mostrarError("Error al cancelar: " + e.getMessage());
        }
    }

    // ─── Validación de paneles ────────────────────────────────────────────────

    private boolean validarPanel1() {
        boolean ok = true;

        String errNombre = txtNombre.getText().trim().isEmpty() ? "El nombre es obligatorio"
                : validarTexto(txtNombre.getText(), "Nombre");
        aplicarEstilo(txtNombre, lblErrorNombre, errNombre);
        if (!errNombre.isEmpty()) ok = false;

        String errApellido = txtApellido.getText().trim().isEmpty() ? "El apellido es obligatorio"
                : validarTexto(txtApellido.getText(), "Apellido");
        aplicarEstilo(txtApellido, lblErrorApellido, errApellido);
        if (!errApellido.isEmpty()) ok = false;

        String errCedula = txtCedula.getText().trim().isEmpty() ? "La cédula es obligatoria"
                : validarNumerico(txtCedula.getText(), "Cédula", 20);
        aplicarEstilo(txtCedula, lblErrorCedula, errCedula);
        if (!errCedula.isEmpty()) ok = false;

        String errCorreo = txtCorreo.getText().trim().isEmpty() ? "El correo es obligatorio"
                : validarCorreo(txtCorreo.getText());
        aplicarEstilo(txtCorreo, lblErrorCorreo, errCorreo);
        if (!errCorreo.isEmpty()) ok = false;

        if (cbGenero.getValue() == null) {
            aplicarEstilo(new Label(), lblErrorGenero, "Seleccione un género");
            ok = false;
        } else {
            aplicarEstilo(new Label(), lblErrorGenero, "");
        }

        if (dpFechaNac.getValue() == null) {
            aplicarEstilo(new Label(), lblErrorFechaNac, "Seleccione fecha de nacimiento");
            ok = false;
        } else {
            aplicarEstilo(new Label(), lblErrorFechaNac, "");
        }

        return ok;
    }

    private boolean validarPanel2() {
        boolean ok = true;

        String errUsuario = txtUsuario.getText().trim().isEmpty() ? "El usuario es obligatorio"
                : validarUsuario(txtUsuario.getText());
        aplicarEstilo(txtUsuario, lblErrorUsuario, errUsuario);
        if (!errUsuario.isEmpty()) ok = false;

        String errContrasena = txtContrasena.getText().isEmpty() ? "La contraseña es obligatoria"
                : validarContrasena(txtContrasena.getText());
        aplicarEstilo(txtContrasena, lblErrorContrasena, errContrasena);
        if (!errContrasena.isEmpty()) ok = false;

        String errConfirmar = txtConfirmarContrasena.getText().isEmpty() ? "Confirme la contraseña"
                : validarConfirmarContrasena();
        aplicarEstilo(txtConfirmarContrasena, lblErrorConfirmarContrasena, errConfirmar);
        if (!errConfirmar.isEmpty()) ok = false;

        return ok;
    }

    private int generoAId(String genero) {
        return Conversiones.generoAId(genero);
    }

    private void irALogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/presentacion/vistas/VistaLogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}
