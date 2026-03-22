package com.proyecto.presentacion.controladores;

import com.proyecto.logica.modelos.Usuario;
import com.proyecto.logica.servicios.ServicioUsuarios;
import com.proyecto.persistencia.repositorios.RepositorioUsuario;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ControladorLogin {

    @FXML private TextField txtUsuario; // Asegúrate de poner fx:id="txtUsuario" en el FXML
    @FXML private PasswordField txtContrasena; // Asegúrate de poner fx:id="txtContrasena" en el FXML

    private ServicioUsuarios servicioUsuarios;

    public ControladorLogin() {
        // Inicializamos el servicio (En un proyecto real usarías Inyección de Dependencias)
        this.servicioUsuarios = new ServicioUsuarios(new RepositorioUsuario());
    }

    @FXML
    public void onBtnIniciarSesionClicked() {
        String user = txtUsuario.getText();
        String pass = txtContrasena.getText();

        Usuario usuario = servicioUsuarios.autenticar(user, pass);

        if (usuario != null) {
            System.out.println("✅ Acceso concedido");
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