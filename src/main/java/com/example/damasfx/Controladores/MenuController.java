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

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    private static final Logger logger = LogManager.getLogger(MenuController.class);

    @FXML
    private Button btnSettings;

    private UserManagement userCollection = DataBase.getInstance().getUserCollection();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserManagement sesionManagement = new UserManagement();
        userCollection.setCurrentUser(userCollection.getUserById(sesionManagement.getLoggedInUser()));
        btnSettings.setVisible(userCollection.getCurrentUser().getRoleType() == RoleType.ADMINISTRADOR || userCollection.getCurrentUser().getRoleType() == RoleType.CREADOR);
    }
    @FXML
    void launchPlayScene(ActionEvent event) {
        logger.info("El usuario se ha dirigido a la pantalla de juego");
        SceneLoader.loadScene("pages/secondUserLogin-view.fxml",event);
    }

    public void onExit(ActionEvent event) {
        logger.info("El usuario ha salido de la aplicación");
        userCollection.clearLoggedInUser();
        SceneLoader.loadScene("pages/start-view.fxml",event);
    }

    @FXML
    public void scoreScene(ActionEvent event) {
        logger.info("El usuario se ha dirigido a la escena de puntuaciones del juego");
        SceneLoader.loadScene("pages/score-view.fxml",event);
    }

    @FXML
    public void goToSettings(ActionEvent event) {
        logger.info("El usuario se ha dirigido a la escena de gestión de usuarios");
        SceneLoader.loadScene("pages/admin-view.fxml", event);
    }
}
