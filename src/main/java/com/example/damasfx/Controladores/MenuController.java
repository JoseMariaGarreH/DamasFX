package com.example.damasfx.Controladores;

import com.example.damasfx.Enumerados.RoleType;
import com.example.damasfx.Gestion.SceneLoader;
import com.example.damasfx.Gestion.UserManagement;
import com.example.damasfx.Modelo.Users;
import com.example.damasfx.VDataBase.DataBase;
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

    private UserManagement userCollection = DataBase.getInstance().getUserCollection();
    private Properties properties = new Properties();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();
        UserManagement sesionManagement = new UserManagement();
        userCollection.setCurrentUser(userCollection.getUserById(sesionManagement.getLoggedInUser()));
        btnSettings.setVisible(userCollection.getCurrentUser().getRoleType() == RoleType.ADMINISTRADOR || userCollection.getCurrentUser().getRoleType() == RoleType.CREADOR);
    }

    private void loadProperties() {
        try{
            InputStream input = SecondUserController.class.getClassLoader().getResourceAsStream("general.properties");
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }
    @FXML
    void launchPlayScene(ActionEvent event) {
        logger.info("El usuario se ha dirigido a la pantalla de juego");
        SceneLoader.loadScene(properties.getProperty("second_user_login"),event);
    }

    public void onExit(ActionEvent event) {
        logger.info("El usuario ha salido de la aplicación");
        userCollection.clearLoggedInUser();
        SceneLoader.loadScene(properties.getProperty("start_view"),event);
    }

    @FXML
    public void scoreScene(ActionEvent event) {
        logger.info("El usuario se ha dirigido a la escena de puntuaciones del juego");
        SceneLoader.loadScene(properties.getProperty("score_view"),event);
    }

    @FXML
    public void goToSettings(ActionEvent event) {
        logger.info("El usuario se ha dirigido a la escena de gestión de usuarios");
        SceneLoader.loadScene(properties.getProperty("admin_view"), event);
    }
}
