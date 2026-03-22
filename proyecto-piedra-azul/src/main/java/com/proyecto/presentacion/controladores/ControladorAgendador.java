package com.proyecto.presentacion.controladores;

import java.time.LocalDate;
import java.util.ResourceBundle;

import com.proyecto.logica.modelos.Cita;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import java.net.URL;

public class ControladorAgendador {

    // Asegúrate de tener estas importaciones
    @FXML
    private DatePicker dpFechaFiltro;
    @FXML
    private ComboBox<String> cbDoctorFiltro; // O ComboBox<Medico> según tu modelo
    @FXML
    private TableView<Cita> tblCitas;

    @FXML
    void onFiltroCambiado(ActionEvent event) {
        LocalDate fechaSeleccionada = dpFechaFiltro.getValue();
        String doctorSeleccionado = cbDoctorFiltro.getValue();

        System.out.println("Filtrando por: " + fechaSeleccionada + " y " + doctorSeleccionado);

        // Aquí llamarías a tu servicio para recargar la tabla
        // cargarCitas(fechaSeleccionada, doctorSeleccionado);
    }

    @FXML
    void onNuevaCita(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/presentacion/vistas/VistaAgendarCita.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) tblCitas.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}