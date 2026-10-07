package com.gestortareas.repository;

import com.gestortareas.model.Tarea;
import javafx.collections.ObservableList;

import java.util.Optional;

/**
 * Repositorio para la persistencia de tareas.
 * Define el contrato para operaciones de lectura y escritura.
 */
public interface TareaRepository {

    /**
     * Obtiene todas las tareas persistidas.
     *
     * @return lista observable de tareas
     */
    ObservableList<Tarea> encontrarTodas();

    /**
     * Guarda la lista completa de tareas, reemplazando cualquier dato existente.
     *
     * @param tareas lista de tareas a persistir
     */
    void guardarTodas(ObservableList<Tarea> tareas);

    /**
     * Busca una tarea por su identificador.
     *
     * @param id identificador de la tarea
     * @return tarea opcional si se encuentra
     */
    Optional<Tarea> buscarPorId(String id);

    /**
     * Elimina una tarea por su identificador.
     *
     * @param id identificador de la tarea
     * @return true si se eliminó, false si no se encontró
     */
    boolean eliminarPorId(String id);
}
