package com.proyecto.presentacion.controladores;

import java.time.LocalDate;
import java.util.ResourceBundle;

import com.proyecto.logica.modelos.Cita;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.Initializable;
import java.net.URL;

public class ControladorAgendador  {

    // Asegúrate de tener estas importaciones
    @FXML private DatePicker dpFechaFiltro;
    @FXML private ComboBox<String> cbDoctorFiltro; // O ComboBox<Medico> según tu modelo
    @FXML private TableView<Cita> tblCitas;

    @FXML
    void onFiltroCambiado(ActionEvent event) {
        LocalDate fechaSeleccionada = dpFechaFiltro.getValue();
        String doctorSeleccionado = cbDoctorFiltro.getValue();
        
        System.out.println("Filtrando por: " + fechaSeleccionada + " y " + doctorSeleccionado);
        
        // Aquí llamarías a tu servicio para recargar la tabla
        // cargarCitas(fechaSeleccionada, doctorSeleccionado);
    }
    
}