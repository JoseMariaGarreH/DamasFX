package com.example.damasfx;

// Importaciones necesarias para la aplicación
import com.example.damasfx.Controllers.MenuController;
import com.example.damasfx.Utils.UserManagement;
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

// Clase principal de la aplicación que extiende Application
public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(MenuController.class);
    private Properties properties = new Properties(); // Propiedades cargadas desde un archivo de configuración

    @Override
    public void start(Stage stage) throws IOException {
        // Crea una instancia de UserManagement para gestionar la sesión de usuario
        UserManagement sessionManager = new UserManagement();
        // Obtiene el nombre del usuario que ha iniciado sesión previamente
        String loggedInUser = sessionManager.getLoggedInUser();
        // Carga las propiedades desde el archivo de configuración
        loadProperties();

        FXMLLoader fxmlLoader;
        if (loggedInUser != null) {
            // Si hay un usuario logueado, carga la pantalla principal
            fxmlLoader = new FXMLLoader(getClass().getResource(properties.getProperty("menu_view")));
        } else {
            // Si no hay un usuario logueado, carga la pantalla de inicio de sesión
            fxmlLoader = new FXMLLoader(getClass().getResource(properties.getProperty("start_view")));
        }

        // Crea una nueva escena con el contenido cargado del archivo FXML
        Scene scene = new Scene(fxmlLoader.load());
        // Añade la hoja de estilos a la escena
        scene.getStylesheets().add(getClass().getResource(properties.getProperty("style")).toExternalForm());

        // Carga y establece el icono de la aplicación
        Image icon = new Image(getClass().getResourceAsStream(properties.getProperty("application_icon")));
        stage.getIcons().add(icon);

        // Configura las propiedades de la ventana principal
        stage.setMinWidth(750);
        stage.setMinHeight(500);
        stage.setTitle("DamasFX");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    // Método para cargar las propiedades desde un archivo de configuración
    private void loadProperties() {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("general.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

    // Método principal de la aplicación que lanza la aplicación JavaFX
    public static void main(String[] args) {
        launch();
    }
}
