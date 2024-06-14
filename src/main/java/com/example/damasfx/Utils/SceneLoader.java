package com.example.damasfx.Utils;

import com.example.damasfx.Main;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneLoader {
    public static void loadScene(String fxmlPath, Event event) {
        try {
            // Carga el archivo fxml
            Stage ventana = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Scene escena;

            // Crea la nueva escena con el tamaño por defecto del archivo FXML
            escena = new Scene(fxmlLoader.load());


            ventana.setScene(escena); // Establece la nueva escena en la ventana actual
            ventana.sizeToScene(); // Ajusta el tamaño de la ventana para que se ajuste al contenido de la escena
            ventana.centerOnScreen(); // Centra la ventana en la pantalla
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
