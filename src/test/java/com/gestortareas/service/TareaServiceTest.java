package com.gestortareas.service;

import com.gestortareas.model.Estado;
import com.gestortareas.model.Prioridad;
import com.gestortareas.model.Tarea;
import com.gestortareas.repository.JsonTareaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TareaServiceTest {

    private File archivoTemp;
    private TareaService tareaService;

    @BeforeEach
    void setUp() throws Exception {
        archivoTemp = File.createTempFile("test_tareas", ".json");
        archivoTemp.deleteOnExit();
        // Inicializar con repositorio temporal
        tareaService = new TareaServiceImpl(new JsonTareaRepository(archivoTemp.getAbsolutePath()));
    }

    @AfterEach
    void tearDown() {
        if (archivoTemp.exists()) {
            archivoTemp.delete();
        }
    }

    @Test
    void testAgregarTarea() {
        int tamanioInicial = tareaService.obtenerTodas().size();
        Tarea nueva = new Tarea("Estudiar Física", "Física I", LocalDate.now().plusDays(3), Prioridad.ALTA, Estado.PENDIENTE);
        
        boolean agregada = tareaService.agregar(nueva);
        assertTrue(agregada);
        assertEquals(tamanioInicial + 1, tareaService.obtenerTodas().size());

        Optional<Tarea> encontrada = tareaService.buscarPorId(nueva.getId());
        assertTrue(encontrada.isPresent());
        assertEquals("Estudiar Física", encontrada.get().getTitulo());
    }

    @Test
    void testActualizarTarea() {
        Tarea tarea = tareaService.obtenerTodas().get(0);
        String id = tarea.getId();

        tarea.setTitulo("Título Modificado");
        tarea.setEstado(Estado.COMPLETADA);
        boolean actualizado = tareaService.actualizar(tarea);

        assertTrue(actualizado);
        Optional<Tarea> encontrada = tareaService.buscarPorId(id);
        assertTrue(encontrada.isPresent());
        assertEquals("Título Modificado", encontrada.get().getTitulo());
        assertEquals(Estado.COMPLETADA, encontrada.get().getEstado());
    }

    @Test
    void testEliminarTarea() {
        Tarea tarea = tareaService.obtenerTodas().get(0);
        String id = tarea.getId();
        int tamanioInicial = tareaService.obtenerTodas().size();

        boolean eliminada = tareaService.eliminar(id);
        assertTrue(eliminada);
        assertEquals(tamanioInicial - 1, tareaService.obtenerTodas().size());
        assertTrue(tareaService.buscarPorId(id).isEmpty());
    }

    @Test
    void testPersistenciaEnArchivo() {
        // Al agregar, verificar que se recupere desde una nueva instancia del servicio
        Tarea nueva = new Tarea("Persistencia Test", "Química", LocalDate.now(), Prioridad.MEDIA, Estado.EN_PROGRESO);
        tareaService.agregar(nueva);

        TareaService nuevoServicio = new TareaServiceImpl(new JsonTareaRepository(archivoTemp.getAbsolutePath()));
        Optional<Tarea> encontrada = nuevoServicio.buscarPorId(nueva.getId());
        assertTrue(encontrada.isPresent());
        assertEquals("Persistencia Test", encontrada.get().getTitulo());
    }
}
