package com.gestortareas.model;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Modelo de datos Tarea con soporte para propiedades observables de JavaFX
 * y métodos estándar (getters/setters tradicionales) para facilitar persistencia
 * por parte de otros componentes o agentes.
 */
public class Tarea {

    private final StringProperty id;
    private final StringProperty titulo;
    private final StringProperty asignatura;
    private final ObjectProperty<LocalDate> fechaEntrega;
    private final ObjectProperty<Prioridad> prioridad;
    private final ObjectProperty<Estado> estado;

    public Tarea() {
        this(UUID.randomUUID().toString(), "", "", LocalDate.now(), Prioridad.MEDIA, Estado.PENDIENTE);
    }

    public Tarea(String titulo, String asignatura, LocalDate fechaEntrega, Prioridad prioridad, Estado estado) {
        this(UUID.randomUUID().toString(), titulo, asignatura, fechaEntrega, prioridad, estado);
    }

    public Tarea(String id, String titulo, String asignatura, LocalDate fechaEntrega, Prioridad prioridad, Estado estado) {
        this.id = new SimpleStringProperty(id != null ? id : UUID.randomUUID().toString());
        this.titulo = new SimpleStringProperty(titulo != null ? titulo : "");
        this.asignatura = new SimpleStringProperty(asignatura != null ? asignatura : "");
        this.fechaEntrega = new SimpleObjectProperty<>(fechaEntrega != null ? fechaEntrega : LocalDate.now());
        this.prioridad = new SimpleObjectProperty<>(prioridad != null ? prioridad : Prioridad.MEDIA);
        this.estado = new SimpleObjectProperty<>(estado != null ? estado : Estado.PENDIENTE);
    }

    // ID
    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    // Título
    public String getTitulo() {
        return titulo.get();
    }

    public void setTitulo(String titulo) {
        this.titulo.set(titulo);
    }

    public StringProperty tituloProperty() {
        return titulo;
    }

    // Asignatura
    public String getAsignatura() {
        return asignatura.get();
    }

    public void setAsignatura(String asignatura) {
        this.asignatura.set(asignatura);
    }

    public StringProperty asignaturaProperty() {
        return asignatura;
    }

    // Fecha Entrega
    public LocalDate getFechaEntrega() {
        return fechaEntrega.get();
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega.set(fechaEntrega);
    }

    public ObjectProperty<LocalDate> fechaEntregaProperty() {
        return fechaEntrega;
    }

    // Prioridad
    public Prioridad getPrioridad() {
        return prioridad.get();
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad.set(prioridad);
    }

    public ObjectProperty<Prioridad> prioridadProperty() {
        return prioridad;
    }

    // Estado
    public Estado getEstado() {
        return estado.get();
    }

    public void setEstado(Estado estado) {
        this.estado.set(estado);
    }

    public ObjectProperty<Estado> estadoProperty() {
        return estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        return Objects.equals(getId(), tarea.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id='" + getId() + '\'' +
                ", titulo='" + getTitulo() + '\'' +
                ", asignatura='" + getAsignatura() + '\'' +
                ", fechaEntrega=" + getFechaEntrega() +
                ", prioridad=" + getPrioridad() +
                ", estado=" + getEstado() +
                '}';
    }
}
