package com.proyecto.presentacion.controladores;

import com.proyecto.logica.interfaces.IServicioAgendamiento;
import com.proyecto.logica.modelos.Cita;
import com.proyecto.logica.modelos.MedicoTerapista;
import com.proyecto.logica.modelos.Paciente;
import com.proyecto.logica.servicios.ServicioAgendamiento;
import com.proyecto.persistencia.interfaces.IRepositorioMedicoTerapista;
import com.proyecto.persistencia.interfaces.IRepositorioPaciente;
import com.proyecto.persistencia.repositorios.RepositorioCitas;
import com.proyecto.persistencia.repositorios.RepositorioJornadaLaboral;
import com.proyecto.persistencia.repositorios.RepositorioMedicoTerapista;
import com.proyecto.persistencia.repositorios.RepositorioPaciente;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ControladorAgendador implements Initializable {

    @FXML private DatePicker dpFechaFiltro;
    @FXML private ComboBox<MedicoTerapista> cbDoctorFiltro;
    @FXML private TableView<Cita> tblCitas;
    @FXML private TableColumn<Cita, String> colPaciente;
    @FXML private TableColumn<Cita, String> colMedico;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colHora;
    @FXML private Label lblTotalHoy;
    @FXML private Label lblPendientesConfirmacionHoy;
    @FXML private Label medicosActivos;

    private IServicioAgendamiento servicioAgendamiento;
    private IRepositorioMedicoTerapista repoMedico;
    private IRepositorioPaciente repoPaciente;

    // Mapas para resolver nombres sin tocar los modelos
    private Map<Integer, String> nombresMedicos;
    private Map<Integer, String> nombresPacientes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        repoMedico = new RepositorioMedicoTerapista();
        repoPaciente = new RepositorioPaciente();
        servicioAgendamiento = new ServicioAgendamiento(
            new RepositorioCitas(),
            new RepositorioJornadaLaboral(),
            repoMedico
        );

        // Precarga los mapas de nombres
        nombresMedicos = repoMedico.listar().stream()
            .collect(Collectors.toMap(
                m -> m.getIdPersona(),
                m -> m.getNombre() + " " + m.getApellido()
            ));

        nombresPacientes = repoPaciente.listar().stream()
            .collect(Collectors.toMap(
                p -> p.getIdPersona(),
                p -> p.getNombre() + " " + p.getApellido()
            ));

        configurarColumnas();
        cargarMedicosEnComboBox();
        cargarCitas(null, null);
        actualizarContadores();
    }

    private void configurarColumnas() {
        colPaciente.setCellValueFactory(cellData -> {
            String nombre = nombresPacientes.getOrDefault(cellData.getValue().getIdPaciente(),
                "Paciente #" + cellData.getValue().getIdPaciente());
            return new SimpleStringProperty(nombre);
        });

        colMedico.setCellValueFactory(cellData -> {
            String nombre = nombresMedicos.getOrDefault(cellData.getValue().getIdMedico(),
                "Médico #" + cellData.getValue().getIdMedico());
            return new SimpleStringProperty(nombre);
        });

        colFecha.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getFecha() != null ? cellData.getValue().getFecha().toString() : ""
            )
        );

        colHora.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getHoraInicio() != null ? cellData.getValue().getHoraInicio().toString() : ""
            )
        );
    }

    private void cargarMedicosEnComboBox() {
        // Usa listar() para traer todos, sin depender del id_estado
        List<MedicoTerapista> medicos = repoMedico.listar();

        ObservableList<MedicoTerapista> items = FXCollections.observableArrayList();
        items.add(null); // Opción "Todos los médicos"
        items.addAll(medicos);

        cbDoctorFiltro.setItems(items);
        cbDoctorFiltro.setConverter(new StringConverter<>() {
            @Override
            public String toString(MedicoTerapista m) {
                return (m == null) ? "Todos los médicos" : m.getNombre() + " " + m.getApellido();
            }
            @Override
            public MedicoTerapista fromString(String s) { return null; }
        });
        cbDoctorFiltro.getSelectionModel().selectFirst();
    }

    private void cargarCitas(Integer prmIdMedico, LocalDate prmFecha) {
        List<Cita> citas = servicioAgendamiento.listarCitas(prmIdMedico, prmFecha);
        tblCitas.setItems(FXCollections.observableArrayList(citas));
    }

    private void actualizarContadores() {
        List<Cita> citasHoy = servicioAgendamiento.listarCitas(null, LocalDate.now());
        if (lblTotalHoy != null)
            lblTotalHoy.setText(String.valueOf(citasHoy.size()));

        long pendientes = citasHoy.stream()
            .filter(c -> c.getIdEstadoCita() != null && c.getIdEstadoCita() == 1)
            .count();
        if (lblPendientesConfirmacionHoy != null)
            lblPendientesConfirmacionHoy.setText(String.valueOf(pendientes));

        if (medicosActivos != null)
            medicosActivos.setText(String.valueOf(nombresMedicos.size()));
    }

    @FXML
    void onFiltrar(ActionEvent event) {
        LocalDate fecha = dpFechaFiltro.getValue();
        MedicoTerapista medicoSeleccionado = cbDoctorFiltro.getValue();
        Integer idMedico = (medicoSeleccionado != null) ? medicoSeleccionado.getIdPersona() : null;
        cargarCitas(idMedico, fecha);
    }
}
