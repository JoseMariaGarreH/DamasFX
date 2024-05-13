package com.example.damasfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().addAll(this.getClass().getResource("style/modelo.css").toExternalForm());

        Image icon = new Image(getClass().getResourceAsStream("/com/example/damasfx/img/logo.jpg"));
        stage.getIcons().add(icon);

        stage.setMinWidth(750);
        stage.setMinHeight(500);
        stage.setTitle("Damas");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}