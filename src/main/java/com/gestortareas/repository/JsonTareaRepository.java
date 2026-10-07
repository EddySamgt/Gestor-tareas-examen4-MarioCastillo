package com.gestortareas.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gestortareas.model.Tarea;
import com.gestortareas.model.TareaDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio basada en archivo JSON usando Jackson.
 * Persiste las tareas en formato JSON en el archivo especificado.
 */
public class JsonTareaRepository implements TareaRepository {

    private final File archivoJSON;
    private final ObjectMapper objectMapper;

    /**
     * Constructor con archivo predeterminado "tareas.json".
     */
    public JsonTareaRepository() {
        this("tareas.json");
    }

    /**
     * Constructor con archivo especificado.
     *
     * @param nombreArchivo nombre o ruta del archivo JSON
     */
    public JsonTareaRepository(String nombreArchivo) {
        this.archivoJSON = new File(nombreArchivo);
        this.objectMapper = new ObjectMapper();
        // Register JavaTimeModule for LocalDate support
        this.objectMapper.registerModule(new JavaTimeModule());
        // Disable serialization of dates as timestamps (default with JavaTimeModule is ISO-8601 string)
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Configure ObjectMapper for pretty printing
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public ObservableList<Tarea> encontrarTodas() {
        if (!archivoJSON.exists()) {
            return FXCollections.observableArrayList();
        }
        try {
            List<TareaDTO> dtos = objectMapper.readValue(archivoJSON, new TypeReference<List<TareaDTO>>() {});
            ObservableList<Tarea> observable = FXCollections.observableArrayList();
            for (TareaDTO dto : dtos) {
                observable.add(dto.toTarea());
            }
            return observable;
        } catch (IOException e) {
            System.err.println("Error al leer tareas desde JSON: " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }

    @Override
    public void guardarTodas(ObservableList<Tarea> tareas) {
        try {
            // Convertir lista de Tarea a lista de TareaDTO para serialización limpia
            java.util.List<TareaDTO> dtos = new java.util.ArrayList<>();
            for (Tarea tarea : tareas) {
                dtos.add(TareaDTO.fromTarea(tarea));
            }
            objectMapper.writeValue(archivoJSON, dtos);
        } catch (IOException e) {
            System.err.println("Error al guardar tareas en JSON: " + e.getMessage());
        }
    }

    @Override
    public Optional<Tarea> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        return encontrarTodas().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    @Override
    public boolean eliminarPorId(String id) {
        if (id == null) return false;
        ObservableList<Tarea> todas = encontrarTodas();
        boolean removida = todas.removeIf(t -> t.getId().equals(id));
        if (removida) {
            guardarTodas(todas);
            return true;
        }
        return false;
    }
}
