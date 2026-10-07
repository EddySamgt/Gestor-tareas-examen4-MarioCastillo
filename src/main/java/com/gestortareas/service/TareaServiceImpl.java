package com.gestortareas.service;

import com.gestortareas.model.Estado;
import com.gestortareas.model.Prioridad;
import com.gestortareas.model.Tarea;
import com.gestortareas.repository.JsonTareaRepository;
import com.gestortareas.repository.TareaRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Implementación del servicio de tareas que utiliza un repositorio JSON para persistencia.
 * Mantiene una lista observable en memoria para un acceso rápido y notificaciones a la UI.
 */
public class TareaServiceImpl implements TareaService {

    private static final String DEFAULT_FILE_NAME = "tareas.json";
    private final TareaRepository tareaRepository;
    private final ObservableList<Tarea> listaTareas = FXCollections.observableArrayList();

    /**
     * Constructor predeterminado que usa el archivo JSON estándar.
     */
    public TareaServiceImpl() {
        this(new JsonTareaRepository(DEFAULT_FILE_NAME));
    }

    /**
     * Constructor con repositorio específico (para inyección de dependencias o testing).
     *
     * @param tareaRepository el repositorio de persistencia a utilizar
     */
    public TareaServiceImpl(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
        cargar();
        // Si no hay datos, cargar algunos de ejemplo para demostración
        if (listaTareas.isEmpty()) {
            cargarDatosEjemplo();
            guardar();
        }
    }

    private void cargarDatosEjemplo() {
        listaTareas.add(new Tarea("Proyecto Final JavaFX", "Programación II", LocalDate.now().plusDays(5), Prioridad.ALTA, Estado.EN_PROGRESO));
        listaTareas.add(new Tarea("Ejercicios de Cálculo Integral", "Matemáticas", LocalDate.now().plusDays(2), Prioridad.MEDIA, Estado.PENDIENTE));
        listaTareas.add(new Tarea("Lectura Capítulo 4", "Historia", LocalDate.now().minusDays(1), Prioridad.BAJA, Estado.COMPLETADA));
    }

    @Override
    public ObservableList<Tarea> obtenerTodas() {
        return listaTareas;
    }

    @Override
    public boolean agregar(Tarea tarea) {
        if (tarea == null) return false;
        boolean added = listaTareas.add(tarea);
        if (added) {
            guardar();
        }
        return added;
    }

    @Override
    public boolean actualizar(Tarea tareaActualizada) {
        if (tareaActualizada == null) return false;
        for (int i = 0; i < listaTareas.size(); i++) {
            if (listaTareas.get(i).getId().equals(tareaActualizada.getId())) {
                Tarea actual = listaTareas.get(i);
                actual.setTitulo(tareaActualizada.getTitulo());
                actual.setAsignatura(tareaActualizada.getAsignatura());
                actual.setFechaEntrega(tareaActualizada.getFechaEntrega());
                actual.setPrioridad(tareaActualizada.getPrioridad());
                actual.setEstado(tareaActualizada.getEstado());
                guardar();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminar(String id) {
        if (id == null) return false;
        boolean removed = listaTareas.removeIf(t -> t.getId().equals(id));
        if (removed) {
            guardar();
        }
        return removed;
    }

    @Override
    public Optional<Tarea> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        return listaTareas.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    @Override
    public void guardar() {
        tareaRepository.guardarTodas(listaTareas);
    }

    @Override
    public void cargar() {
        ObservableList<Tarea> cargadas = tareaRepository.encontrarTodas();
        listaTareas.setAll(cargadas);
    }
}
