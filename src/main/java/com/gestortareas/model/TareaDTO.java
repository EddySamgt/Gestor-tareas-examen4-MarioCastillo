package com.gestortareas.model;

import java.time.LocalDate;

/**
 * Clase de transferencia de datos (DTO) para la serialización/deserialización JSON de tareas.
 * Evita serializar propiedades de JavaFX que no son necesarias para la persistencia.
 */
public class TareaDTO {

    private String id;
    private String titulo;
    private String asignatura;
    private LocalDate fechaEntrega;
    private Prioridad prioridad;
    private Estado estado;

    // Constructores
    public TareaDTO() {
    }

    public TareaDTO(String id, String titulo, String asignatura, LocalDate fechaEntrega,
                    Prioridad prioridad, Estado estado) {
        this.id = id;
        this.titulo = titulo;
        this.asignatura = asignatura;
        this.fechaEntrega = fechaEntrega;
        this.prioridad = prioridad;
        this.estado = estado;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    // Método de conveniencia para convertir a Tarea (con propiedades)
    public Tarea toTarea() {
        return new Tarea(id, titulo, asignatura, fechaEntrega, prioridad, estado);
    }

    // Método de conveniencia para crear DTO desde Tarea
    public static TareaDTO fromTarea(Tarea tarea) {
        return new TareaDTO(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getAsignatura(),
                tarea.getFechaEntrega(),
                tarea.getPrioridad(),
                tarea.getEstado()
        );
    }
}
