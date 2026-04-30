package com.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/presentacion/vistas/VistaLogin.fxml"));

        stage.setTitle("Piedra Azul");
        stage.setScene(new Scene(root));
        configurarStage(stage);
        stage.show();
    }

    private void configurarStage(Stage stage) {
        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();

        double ancho = Math.max(900,  pantalla.getWidth()  * 0.80);
        double alto  = Math.max(620,  pantalla.getHeight() * 0.80);

        stage.setWidth(ancho);
        stage.setHeight(alto);
        stage.setX(pantalla.getMinX() + (pantalla.getWidth()  - ancho) / 2);
        stage.setY(pantalla.getMinY() + (pantalla.getHeight() - alto)  / 2);
        stage.setMinWidth(800);
        stage.setMinHeight(580);
        stage.setResizable(true);
    }
}
