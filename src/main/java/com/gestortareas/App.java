package com.gestortareas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de entrada principal de la aplicación JavaFX Gestor de Tareas Académicas.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestortareas/view/MainView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1080, 680);
            primaryStage.setTitle("Gestor de Tareas Académicas");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(850);
            primaryStage.setMinHeight(550);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la interfaz principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
