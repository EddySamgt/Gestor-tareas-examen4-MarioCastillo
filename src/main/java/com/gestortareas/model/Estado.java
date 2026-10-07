package com.gestortareas.model;

public enum Estado {
    PENDIENTE("Pendiente"),
    EN_PROGRESO("En Progreso"),
    COMPLETADA("Completada");

    private final String etiqueta;

    Estado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }

    public static Estado fromString(String text) {
        for (Estado e : Estado.values()) {
            if (e.etiqueta.equalsIgnoreCase(text) || e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return PENDIENTE;
    }
}
