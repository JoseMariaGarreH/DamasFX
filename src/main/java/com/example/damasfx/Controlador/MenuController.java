package com.example.damasfx.Controlador;

import com.example.damasfx.Main;
import com.example.damasfx.Modelo.SceneLoader;
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
        SceneLoader.loadScene("play-view.fxml",event);
    }

    public void onExit(ActionEvent event) {
        Platform.exit();
    }
}
