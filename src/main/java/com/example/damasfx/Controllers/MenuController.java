package com.example.damasfx.Controllers;

import com.example.damasfx.Enums.RoleType;
import com.example.damasfx.Utils.SceneLoader;
import com.example.damasfx.Utils.UserManagement;
import com.example.damasfx.Services.DataBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;


public class MenuController implements Initializable {
    private static final Logger logger = LogManager.getLogger(MenuController.class);

    @FXML
    private Button btnSettings;

    private UserManagement userCollection = DataBase.getInstance().getUserCollection(); // Gestión de usuarios

    private Properties properties = new Properties(); // Propiedades cargadas desde un archivo de configuración


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();

        // Gestionar la sesión del usuario
        UserManagement sesionManagement = new UserManagement();
        userCollection.setCurrentUser(userCollection.getUserById(sesionManagement.getLoggedInUser()));

        // Configurar la visibilidad del botón de configuración basado en el rol del usuario
        btnSettings.setVisible(userCollection.getCurrentUser().getRoleType() == RoleType.ADMINISTRADOR ||
                userCollection.getCurrentUser().getRoleType() == RoleType.CREADOR);
    }



    private void loadProperties() {
        try {
            InputStream input = SecondUserController.class.getClassLoader().getResourceAsStream("general.properties");
            properties.load(input);
        } catch (IOException ex) {
            // Registrar un mensaje de error si no se puede cargar el archivo de propiedades
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

    // Método para manejar el evento de lanzamiento de la escena de juego.
    @FXML
    void launchPlayScene(ActionEvent event) {
        // Registrar un mensaje informativo y cargar la escena de juego
        logger.info("El usuario se ha dirigido a la pantalla de juego");
        SceneLoader.loadScene(properties.getProperty("second_user_login"), event);
    }

    // Método para manejar el evento de salida de la aplicación.
    @FXML
    public void onExit(ActionEvent event) {
        // Registrar un mensaje informativo, limpiar la sesión y cargar la escena de inicio
        logger.info("El usuario ha salido de la aplicación");
        userCollection.clearLoggedInUser();
        SceneLoader.loadScene(properties.getProperty("start_view"), event);
    }

    // Método para manejar el evento de mostrar la escena de puntuaciones.
    @FXML
    public void scoreScene(ActionEvent event) {
        // Registrar un mensaje informativo y cargar la escena de puntuaciones
        logger.info("El usuario se ha dirigido a la escena de puntuaciones del juego");
        SceneLoader.loadScene(properties.getProperty("score_view"), event);
    }

    // Método para manejar el evento de ir a la configuración.
    @FXML
    public void goToSettings(ActionEvent event) {
        // Registrar un mensaje informativo y cargar la escena de gestión de usuarios
        logger.info("El usuario se ha dirigido a la escena de gestión de usuarios");
        SceneLoader.loadScene(properties.getProperty("admin_view"), event);
    }
}
