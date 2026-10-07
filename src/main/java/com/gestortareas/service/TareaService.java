package com.gestortareas.service;

import com.gestortareas.model.Tarea;
import javafx.collections.ObservableList;

import java.util.Optional;

/**
 * Contrato de servicio para la gestión y persistencia de tareas.
 * Diseñado para desacoplar la interfaz gráfica de la persistencia de datos,
 * permitiendo al agente de arquitectura/persistencia implementar cualquier
 * mecanismo (archivo JSON, CSV, base de datos SQLite, etc.).
 */
public interface TareaService {

    /**
     * Retorna la lista observable de tareas actualmente cargadas.
     */
    ObservableList<Tarea> obtenerTodas();

    /**
     * Agrega una nueva tarea y persiste los cambios.
     */
    boolean agregar(Tarea tarea);

    /**
     * Actualiza una tarea existente y persiste los cambios.
     */
    boolean actualizar(Tarea tarea);

    /**
     * Elimina una tarea por su identificador y persiste los cambios.
     */
    boolean eliminar(String id);

    /**
     * Busca una tarea por su identificador.
     */
    Optional<Tarea> buscarPorId(String id);

    /**
     * Fuerza el guardado de los datos pendientes a almacenamiento secundario.
     */
    void guardar();

    /**
     * Carga las tareas desde el almacenamiento persistente.
     */
    void cargar();
}
