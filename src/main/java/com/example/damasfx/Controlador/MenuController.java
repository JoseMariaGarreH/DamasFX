package com.example.damasfx.Controlador;

import com.example.damasfx.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MenuController {


    @FXML
    void launchPlayScene(ActionEvent event) {
        try {
            Stage ventana = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("play-view.fxml"));
            Scene escena;
            ventana.setResizable(true);
            if (ventana.isMaximized()) {
                escena = new Scene(fxmlLoader.load(), ventana.getWidth(), ventana.getHeight());
            } else {
                escena = new Scene(fxmlLoader.load());
            }
            ventana.setScene(escena);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void onExit(ActionEvent event) {
        Platform.exit();
    }
}
