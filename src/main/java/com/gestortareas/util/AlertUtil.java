package com.gestortareas.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Utilidades para desplegar alertas accesibles, claras y consistentes
 * sin interrumpir bruscamente el flujo de la aplicación.
 */
public final class AlertUtil {

    private AlertUtil() {
    }

    public static void mostrarError(String titulo, String cabecera, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public static void mostrarAdvertencia(String titulo, String cabecera, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public static void mostrarInformacion(String titulo, String cabecera, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public static boolean confirmar(String titulo, String cabecera, String contenido) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);

        Optional<ButtonType> respuesta = alert.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }
}
