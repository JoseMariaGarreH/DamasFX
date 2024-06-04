package com.example.damasfx;

import com.example.damasfx.Controladores.MenuController;
import com.example.damasfx.Controladores.RegisterController;
import com.example.damasfx.Gestion.UserManagement;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main extends Application {
    private static final Logger logger = LogManager.getLogger(MenuController.class);
    private Properties properties = new Properties();

    @Override
    public void start(Stage stage) throws IOException {
        UserManagement sessionManager = new UserManagement();
        String loggedInUser = sessionManager.getLoggedInUser();
        loadProperties();

        FXMLLoader fxmlLoader;
        if (loggedInUser != null) {
            // Cargar la pantalla principal
            fxmlLoader = new FXMLLoader(getClass().getResource("pages/menu-view.fxml"));
        } else {
            // Cargar la pantalla de login
            fxmlLoader = new FXMLLoader(getClass().getResource("pages/start-view.fxml"));
        }

        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/com/example/damasfx/style/modelo.css").toExternalForm());

        Image icon = new Image(getClass().getResourceAsStream(properties.getProperty("application_icon")));
        stage.getIcons().add(icon);

        stage.setMinWidth(750);
        stage.setMinHeight(500);
        stage.setTitle("Damas");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void loadProperties() {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("general.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}