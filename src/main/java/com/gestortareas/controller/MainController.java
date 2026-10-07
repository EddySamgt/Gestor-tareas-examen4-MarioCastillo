package com.gestortareas.controller;

import com.gestortareas.model.Estado;
import com.gestortareas.model.Prioridad;
import com.gestortareas.model.Tarea;
import com.gestortareas.service.TareaService;
import com.gestortareas.service.TareaServiceImpl;
import com.gestortareas.util.AlertUtil;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controlador de la vista principal del Gestor de Tareas Académicas.
 * Gestiona el formulario, las validaciones de entrada, el TableView,
 * las interacciones CRUD y el filtrado multifactor.
 */
public class MainController implements Initializable {

    // Controles del Formulario
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAsignatura;
    @FXML private DatePicker dpFechaEntrega;
    @FXML private ComboBox<Prioridad> cbPrioridad;
    @FXML private ComboBox<Estado> cbEstado;

    // Botones de Acción
    @FXML private Button btnAgregar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    // Controles de Búsqueda y Filtros
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbFiltroAsignatura;
    @FXML private ComboBox<String> cbFiltroEstado;
    @FXML private Button btnLimpiarFiltros;

    // TableView y Columnas
    @FXML private TableView<Tarea> tablaTareas;
    @FXML private TableColumn<Tarea, String> colTitulo;
    @FXML private TableColumn<Tarea, String> colAsignatura;
    @FXML private TableColumn<Tarea, LocalDate> colFechaEntrega;
    @FXML private TableColumn<Tarea, Prioridad> colPrioridad;
    @FXML private TableColumn<Tarea, Estado> colEstado;

    // Etiquetas informativas
    @FXML private Label lblContadorTareas;
    @FXML private Label lblEstadoModo;

    // Servicios y Modelo
    private TareaService tareaService;
    private FilteredList<Tarea> listaFiltrada;
    private Tarea tareaSeleccionadaActual;

    private static final String TODAS_OPCION = "Todas";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MainController() {
        this.tareaService = new TareaServiceImpl();
    }

    public void setTareaService(TareaService tareaService) {
        if (tareaService != null) {
            this.tareaService = tareaService;
            configurarColecciones();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarControlesFormulario();
        configurarColumnasTabla();
        configurarColecciones();
        configurarListeners();
        configurarFiltros();
        actualizarModoEdicion(null);
    }

    private void configurarControlesFormulario() {
        cbPrioridad.setItems(FXCollections.observableArrayList(Prioridad.values()));
        cbPrioridad.setValue(Prioridad.MEDIA);

        cbEstado.setItems(FXCollections.observableArrayList(Estado.values()));
        cbEstado.setValue(Estado.PENDIENTE);

        dpFechaEntrega.setValue(LocalDate.now());
    }

    private void configurarColumnasTabla() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAsignatura.setCellValueFactory(new PropertyValueFactory<>("asignatura"));
        colFechaEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Formateador legible para fecha
        colFechaEntrega.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (empty || fecha == null) {
                    setText(null);
                } else {
                    setText(fecha.format(FORMATO_FECHA));
                }
            }
        });

        // Estilización contextual para columna de estado
        colEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Estado item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getEtiqueta());
                    if (item == Estado.COMPLETADA) {
                        setStyle("-fx-text-fill: #1b5e20; -fx-font-weight: bold;");
                    } else if (item == Estado.EN_PROGRESO) {
                        setStyle("-fx-text-fill: #0d47a1; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #e65100; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Estilización contextual para columna de prioridad
        colPrioridad.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Prioridad item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getEtiqueta());
                    if (item == Prioridad.ALTA) {
                        setStyle("-fx-text-fill: #b71c1c; -fx-font-weight: bold;");
                    } else if (item == Prioridad.MEDIA) {
                        setStyle("-fx-text-fill: #f57f17;");
                    } else {
                        setStyle("-fx-text-fill: #2e7d32;");
                    }
                }
            }
        });
    }

    private void configurarColecciones() {
        listaFiltrada = new FilteredList<>(tareaService.obtenerTodas(), p -> true);
        SortedList<Tarea> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaTareas.comparatorProperty());
        tablaTareas.setItems(listaOrdenada);

        actualizarOpcionesFiltroAsignatura();
        actualizarOpcionesFiltroEstado();
        actualizarContador();

        tareaService.obtenerTodas().addListener((javafx.beans.Observable o) -> {
            actualizarOpcionesFiltroAsignatura();
            actualizarContador();
        });
    }

    private void configurarListeners() {
        // Cargar datos en el formulario al seleccionar una tarea en la tabla
        tablaTareas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cargarTareaEnFormulario(newSel);
            }
        });

        // Limpiar estilos de error al interactuar con los campos
        txtTitulo.textProperty().addListener((obs, oldVal, newVal) -> txtTitulo.getStyleClass().remove("input-error"));
        txtAsignatura.textProperty().addListener((obs, oldVal, newVal) -> txtAsignatura.getStyleClass().remove("input-error"));
        dpFechaEntrega.valueProperty().addListener((obs, oldVal, newVal) -> dpFechaEntrega.getStyleClass().remove("input-error"));
        cbPrioridad.valueProperty().addListener((obs, oldVal, newVal) -> cbPrioridad.getStyleClass().remove("input-error"));
        cbEstado.valueProperty().addListener((obs, oldVal, newVal) -> cbEstado.getStyleClass().remove("input-error"));
    }

    private void configurarFiltros() {
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        cbFiltroAsignatura.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        cbFiltroEstado.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        String textoBusqueda = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String asignaturaSeleccionada = cbFiltroAsignatura.getValue();
        String estadoSeleccionado = cbFiltroEstado.getValue();

        listaFiltrada.setPredicate(tarea -> {
            // Filtro por texto clave (título o asignatura)
            if (!textoBusqueda.isEmpty()) {
                boolean coincideTitulo = tarea.getTitulo() != null && tarea.getTitulo().toLowerCase().contains(textoBusqueda);
                boolean coincideAsignatura = tarea.getAsignatura() != null && tarea.getAsignatura().toLowerCase().contains(textoBusqueda);
                if (!coincideTitulo && !coincideAsignatura) {
                    return false;
                }
            }

            // Filtro por asignatura
            if (asignaturaSeleccionada != null && !asignaturaSeleccionada.equals(TODAS_OPCION)) {
                if (tarea.getAsignatura() == null || !tarea.getAsignatura().equalsIgnoreCase(asignaturaSeleccionada)) {
                    return false;
                }
            }

            // Filtro por estado
            if (estadoSeleccionado != null && !estadoSeleccionado.equals(TODAS_OPCION)) {
                if (tarea.getEstado() == null || !tarea.getEstado().getEtiqueta().equalsIgnoreCase(estadoSeleccionado)) {
                    return false;
                }
            }

            return true;
        });

        actualizarContador();
    }

    private void actualizarOpcionesFiltroAsignatura() {
        String seleccionPrevia = cbFiltroAsignatura.getValue();
        var asignaturas = tareaService.obtenerTodas().stream()
                .map(Tarea::getAsignatura)
                .filter(a -> a != null && !a.trim().isEmpty())
                .distinct()
                .sorted()
                .toList();

        var opciones = FXCollections.observableArrayList(TODAS_OPCION);
        opciones.addAll(asignaturas);
        cbFiltroAsignatura.setItems(opciones);

        if (seleccionPrevia != null && opciones.contains(seleccionPrevia)) {
            cbFiltroAsignatura.setValue(seleccionPrevia);
        } else {
            cbFiltroAsignatura.setValue(TODAS_OPCION);
        }
    }

    private void actualizarOpcionesFiltroEstado() {
        var opciones = FXCollections.observableArrayList(TODAS_OPCION);
        for (Estado e : Estado.values()) {
            opciones.add(e.getEtiqueta());
        }
        cbFiltroEstado.setItems(opciones);
        cbFiltroEstado.setValue(TODAS_OPCION);
    }

    private void actualizarContador() {
        int total = tareaService.obtenerTodas().size();
        int mostradas = listaFiltrada.size();
        if (total == mostradas) {
            lblContadorTareas.setText(total + " tareas registradas");
        } else {
            lblContadorTareas.setText("Mostrando " + mostradas + " de " + total + " tareas");
        }
    }

    private void cargarTareaEnFormulario(Tarea tarea) {
        tareaSeleccionadaActual = tarea;
        txtTitulo.setText(tarea.getTitulo());
        txtAsignatura.setText(tarea.getAsignatura());
        dpFechaEntrega.setValue(tarea.getFechaEntrega());
        cbPrioridad.setValue(tarea.getPrioridad());
        cbEstado.setValue(tarea.getEstado());
        actualizarModoEdicion(tarea);
    }

    private void actualizarModoEdicion(Tarea tarea) {
        if (tarea != null) {
            lblEstadoModo.setText("Modo edición: Editando tarea seleccionada");
            lblEstadoModo.setStyle("-fx-text-fill: #1565c0; -fx-font-weight: bold;");
            btnEditar.setDisable(false);
            btnEliminar.setDisable(false);
        } else {
            lblEstadoModo.setText("Modo registro: Creando nueva tarea");
            lblEstadoModo.setStyle("-fx-text-fill: #555555; -fx-font-weight: normal;");
            btnEditar.setDisable(true);
            btnEliminar.setDisable(true);
        }
    }

    // ACCIONES DE BOTONES

    @FXML
    public void onAgregar() {
        if (!validarFormulario()) {
            return;
        }

        Tarea nuevaTarea = new Tarea(
                txtTitulo.getText().trim(),
                txtAsignatura.getText().trim(),
                dpFechaEntrega.getValue(),
                cbPrioridad.getValue(),
                cbEstado.getValue()
        );

        boolean exito = tareaService.agregar(nuevaTarea);
        if (exito) {
            AlertUtil.mostrarInformacion(
                    "Operación exitosa",
                    "Tarea registrada",
                    "La tarea \"" + nuevaTarea.getTitulo() + "\" se guardó correctamente."
            );
            onLimpiar();
        } else {
            AlertUtil.mostrarError(
                    "Error al guardar",
                    "No se pudo registrar la tarea",
                    "Ocurrió un problema al intentar guardar la información. Por favor, inténtalo de nuevo."
            );
        }
    }

    @FXML
    public void onEditar() {
        if (tareaSeleccionadaActual == null) {
            AlertUtil.mostrarAdvertencia(
                    "Atención",
                    "Ninguna tarea seleccionada",
                    "Por favor selecciona primero la tarea que deseas editar en la tabla."
            );
            return;
        }

        if (!validarFormulario()) {
            return;
        }

        tareaSeleccionadaActual.setTitulo(txtTitulo.getText().trim());
        tareaSeleccionadaActual.setAsignatura(txtAsignatura.getText().trim());
        tareaSeleccionadaActual.setFechaEntrega(dpFechaEntrega.getValue());
        tareaSeleccionadaActual.setPrioridad(cbPrioridad.getValue());
        tareaSeleccionadaActual.setEstado(cbEstado.getValue());

        boolean exito = tareaService.actualizar(tareaSeleccionadaActual);
        if (exito) {
            tablaTareas.refresh();
            AlertUtil.mostrarInformacion(
                    "Operación exitosa",
                    "Tarea actualizada",
                    "Los cambios de la tarea \"" + tareaSeleccionadaActual.getTitulo() + "\" han sido guardados con éxito."
            );
            onLimpiar();
        } else {
            AlertUtil.mostrarError(
                    "Error al guardar",
                    "No se pudo actualizar la tarea",
                    "Ocurrió un problema al intentar guardar las modificaciones. Por favor, verifica los datos."
            );
        }
    }

    @FXML
    public void onEliminar() {
        Tarea seleccionada = tablaTareas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            AlertUtil.mostrarAdvertencia(
                    "Atención",
                    "Ninguna tarea seleccionada",
                    "Selecciona en la tabla la tarea que deseas eliminar."
            );
            return;
        }

        boolean confirmado = AlertUtil.confirmar(
                "Confirmación de eliminación",
                "¿Estás seguro de eliminar esta tarea académica?",
                "Tarea: \"" + seleccionada.getTitulo() + "\"\nAsignatura: " + seleccionada.getAsignatura() +
                "\n\nEsta acción eliminará el registro de forma permanente."
        );

        if (confirmado) {
            boolean exito = tareaService.eliminar(seleccionada.getId());
            if (exito) {
                AlertUtil.mostrarInformacion(
                        "Operación exitosa",
                        "Tarea eliminada",
                        "La tarea seleccionada ha sido eliminada del gestor académico."
                );
                onLimpiar();
            } else {
                AlertUtil.mostrarError(
                        "Error al eliminar",
                        "No se pudo eliminar la tarea",
                        "Ocurrió un problema al intentar eliminar el registro seleccionado."
                );
            }
        }
    }

    @FXML
    public void onLimpiar() {
        limpiarErroresVisuales();
        txtTitulo.clear();
        txtAsignatura.clear();
        dpFechaEntrega.setValue(LocalDate.now());
        cbPrioridad.setValue(Prioridad.MEDIA);
        cbEstado.setValue(Estado.PENDIENTE);
        tablaTareas.getSelectionModel().clearSelection();
        tareaSeleccionadaActual = null;
        actualizarModoEdicion(null);
    }

    @FXML
    public void onLimpiarFiltros() {
        txtBuscar.clear();
        cbFiltroAsignatura.setValue(TODAS_OPCION);
        cbFiltroEstado.setValue(TODAS_OPCION);
    }

    private boolean validarFormulario() {
        limpiarErroresVisuales();
        StringBuilder errores = new StringBuilder();

        if (txtTitulo.getText() == null || txtTitulo.getText().trim().isEmpty()) {
            errores.append("• El título de la tarea es obligatorio.\n");
            marcarError(txtTitulo);
        }

        if (txtAsignatura.getText() == null || txtAsignatura.getText().trim().isEmpty()) {
            errores.append("• La asignatura es obligatoria.\n");
            marcarError(txtAsignatura);
        }

        if (dpFechaEntrega.getValue() == null) {
            errores.append("• Debe seleccionar una fecha de entrega válida.\n");
            marcarError(dpFechaEntrega);
        }

        if (cbPrioridad.getValue() == null) {
            errores.append("• Debe seleccionar una prioridad para la tarea.\n");
            marcarError(cbPrioridad);
        }

        if (cbEstado.getValue() == null) {
            errores.append("• Debe seleccionar el estado de la tarea.\n");
            marcarError(cbEstado);
        }

        if (errores.length() > 0) {
            AlertUtil.mostrarAdvertencia(
                    "Campos requeridos",
                    "No se pudo completar la operación",
                    "Por favor, revisa los campos señalados en rojo:\n\n" + errores.toString()
            );
            return false;
        }

        return true;
    }

    private void marcarError(Control control) {
        if (!control.getStyleClass().contains("input-error")) {
            control.getStyleClass().add("input-error");
        }
    }

    private void limpiarErroresVisuales() {
        txtTitulo.getStyleClass().remove("input-error");
        txtAsignatura.getStyleClass().remove("input-error");
        dpFechaEntrega.getStyleClass().remove("input-error");
        cbPrioridad.getStyleClass().remove("input-error");
        cbEstado.getStyleClass().remove("input-error");
    }
}
