package com.example.damasfx.Gestion;

import com.example.damasfx.Main;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneLoader {
    public static void loadScene(String fxmlPath, Event event) {
        try {
            Stage ventana = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Scene escena;

            if (ventana.isMaximized()) {
                escena = new Scene(fxmlLoader.load(), ventana.getWidth(), ventana.getHeight());
            } else {
                escena = new Scene(fxmlLoader.load());
            }
            ventana.setScene(escena);

            // Si se carga una escena diferente a play-view, salir de pantalla completa
            ventana.setFullScreen(fxmlPath.equalsIgnoreCase("pages/play-view.fxml"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
