package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.dto.LoginResponseDTO;
import com.proyecto.presentacion.facade.BackendFacade;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ControladorLogin {

    @FXML private TextField     txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label         lblErrorUsuario;
    @FXML private Button        btnLogin;

    // Notificación inline
    @FXML private HBox  boxNotificacion;
    @FXML private Label lblNotiTitulo;
    @FXML private Label lblNotiMensaje;

    private final BackendFacade backendFacade = new BackendFacade();

    // ─── Inicialización ───────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        lblErrorUsuario.setText("");
        btnLogin.setDisable(true);

        txtUsuario.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 25) { txtUsuario.setText(oldVal); return; }
            if (newVal.isEmpty()) {
                txtUsuario.setStyle("");
                lblErrorUsuario.setText("");
                btnLogin.setDisable(true);
                return;
            }
            String error = validarUsuario(newVal);
            if (!error.isEmpty()) {
                txtUsuario.setStyle("-fx-border-color: #e53935; -fx-border-width: 2;");
                lblErrorUsuario.setText(error);
                btnLogin.setDisable(true);
            } else {
                txtUsuario.setStyle("-fx-border-color: #43a047; -fx-border-width: 2;");
                lblErrorUsuario.setText("");
                btnLogin.setDisable(false);
            }
        });

        // Ocultar notificación al escribir
        txtUsuario.textProperty().addListener((o, a, b) -> ocultarNotificacion());
        txtContrasena.textProperty().addListener((o, a, b) -> ocultarNotificacion());
    }

    // ─── Validación de front ──────────────────────────────────────────────────

    private String validarUsuario(String user) {
        if (user.startsWith(" "))  return "No puede iniciar con espacio";
        if (user.endsWith(" "))    return "No puede terminar con espacio";
        if (user.contains("  "))   return "No se permiten espacios dobles";
        return "";
    }

    /** Devuelve un mensaje de error si los campos no pasan validación, o null si todo está bien. */
    private String validarCampos(String user, String pass) {
        if (user.isBlank())  return "El usuario no puede estar vacío.";
        if (pass.isBlank())  return "La contraseña no puede estar vacía.";
        if (user.length() < 3) return "El usuario debe tener al menos 3 caracteres.";
        if (pass.length() < 4) return "La contraseña debe tener al menos 4 caracteres.";
        return null;
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @FXML
    public void onBtnIniciarSesionClicked() {
        String user = txtUsuario.getText().trim();
        String pass = txtContrasena.getText();

        // Validación de front primero
        String errorFront = validarCampos(user, pass);
        if (errorFront != null) {
            mostrarNotificacion("Datos incompletos", errorFront, TipoNoti.ADVERTENCIA);
            return;
        }

        try {
            LoginResponseDTO dto = backendFacade.login(user, pass);

            if (dto.getToken() == null) {
                mostrarNotificacion("Acceso denegado", "Usuario o contraseña incorrectos.", TipoNoti.ERROR);
                sacudirCampos();
                return;
            }

            SesionUsuario.getInstancia().setToken(dto.getToken());
            SesionUsuario.getInstancia().setIdUsuario(dto.getIdUsuario());
            SesionUsuario.getInstancia().setRol(dto.getRol().toLowerCase());

            navegarSegunRol(dto.getRol().toLowerCase());

        } catch (com.fasterxml.jackson.core.JacksonException e) {
            // El servidor respondió pero con un body que no es el DTO esperado
            // → credenciales incorrectas (el backend devolvió 401 con mensaje de error)
            mostrarNotificacion("Credenciales incorrectas", "Usuario o contraseña incorrectos.", TipoNoti.ERROR);
            sacudirCampos();
        } catch (Exception e) {
            // Error de red real: servidor apagado, timeout, etc.
            mostrarNotificacion("Sin conexión", "No se pudo conectar al servidor. Verifique que el servicio esté activo.", TipoNoti.ERROR);
        }
    }

    // ─── Notificación elegante ────────────────────────────────────────────────

    private enum TipoNoti { ERROR, ADVERTENCIA, EXITO }

    private void mostrarNotificacion(String titulo, String mensaje, TipoNoti tipo) {
        lblNotiTitulo.setText(titulo);
        lblNotiMensaje.setText(mensaje);

        // Color según tipo
        String fondo, borde, texto;
        String icono;
        switch (tipo) {
            case ADVERTENCIA -> {
                fondo  = "#fff8e1"; borde = "#f9a825"; texto = "#e65100"; icono = "⚠";
            }
            case EXITO -> {
                fondo  = "#e8f5e9"; borde = "#43a047"; texto = "#1b5e20"; icono = "✔";
            }
            default -> { // ERROR
                fondo  = "#fdecea"; borde = "#e53935"; texto = "#b71c1c"; icono = "✖";
            }
        }

        boxNotificacion.setStyle(
            "-fx-padding: 12 16 12 16;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: " + fondo + ";" +
            "-fx-border-color: " + borde + ";" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.5;"
        );
        lblNotiTitulo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + texto + ";");
        lblNotiMensaje.setStyle("-fx-font-size: 12px; -fx-text-fill: " + texto + "; -fx-wrap-text: true;");

        // Mostrar con fade-in
        boxNotificacion.setVisible(true);
        boxNotificacion.setManaged(true);
        boxNotificacion.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), boxNotificacion);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Auto-ocultar después de 5 segundos
        PauseTransition pausa = new PauseTransition(Duration.seconds(5));
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), boxNotificacion);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> ocultarNotificacion());

        new SequentialTransition(fadeIn, pausa, fadeOut).play();
    }

    @FXML
    public void onCerrarNotificacion() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), boxNotificacion);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> ocultarNotificacion());
        fadeOut.play();
    }

    private void ocultarNotificacion() {
        boxNotificacion.setVisible(false);
        boxNotificacion.setManaged(false);
    }

    /** Efecto visual de sacudida en los campos cuando las credenciales son incorrectas */
    private void sacudirCampos() {
        for (javafx.scene.Node campo : new javafx.scene.Node[]{txtUsuario, txtContrasena}) {
            javafx.animation.TranslateTransition tt =
                new javafx.animation.TranslateTransition(Duration.millis(60), campo);
            tt.setByX(6);
            tt.setCycleCount(6);
            tt.setAutoReverse(true);
            tt.play();
        }
    }

    // ─── Navegación ───────────────────────────────────────────────────────────

    private void navegarSegunRol(String rol) {
        String vista = switch (rol) {
            case "administrador" -> "/com/presentacion/vistas/VistaAdmin.fxml";
            case "agendador"     -> "/com/presentacion/vistas/VistaAgendador.fxml";
            case "paciente"      -> "/com/presentacion/vistas/VistaPaciente.fxml";
            default              -> "/com/presentacion/vistas/VistaAgendador.fxml";
        };
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));
            Parent root = loader.load();
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
