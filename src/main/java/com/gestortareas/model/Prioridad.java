package com.gestortareas.model;

public enum Prioridad {
    ALTA("Alta"),
    MEDIA("Media"),
    BAJA("Baja");

    private final String etiqueta;

    Prioridad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }

    public static Prioridad fromString(String text) {
        for (Prioridad p : Prioridad.values()) {
            if (p.etiqueta.equalsIgnoreCase(text) || p.name().equalsIgnoreCase(text)) {
                return p;
            }
        }
        return MEDIA;
    }
}
