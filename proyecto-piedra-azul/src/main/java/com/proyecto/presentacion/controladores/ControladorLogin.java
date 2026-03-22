package com.proyecto.presentacion.controladores;

import com.proyecto.logica.servicios.ServicioUsuarios;
import com.proyecto.logica.servicios.ServicioAuth;
import com.proyecto.persistencia.repositorios.RepositorioUsuario;
import com.proyecto.presentacion.SesionUsuario;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ControladorLogin {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasena;

    private ServicioAuth servicioAuth;

    public ControladorLogin() {
        ServicioUsuarios servicioUsuarios = new ServicioUsuarios(new RepositorioUsuario());
        this.servicioAuth = new ServicioAuth(servicioUsuarios);
    }

    @FXML
    public void onBtnIniciarSesionClicked() {

        String user = txtUsuario.getText();
        String pass = txtContrasena.getText();

        String token = servicioAuth.login(user, pass);

        if (token != null) {

            System.out.println(" Token generado: " + token);

            // Guardar token en sesión
            SesionUsuario.getInstancia().setToken(token);

            navegarAPanelPrincipal();

        } else {
            mostrarError("Acceso Denegado", "Usuario o contraseña incorrectos.");
        }
    }

    private void navegarAPanelPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/presentacion/vistas/VistaAgendador.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}