package com.example.damasfx.Controladores;

import com.example.damasfx.Gestion.SceneLoader;
import com.example.damasfx.Modelo.Scores;
import com.example.damasfx.Gestion.UserManagement;
import com.example.damasfx.Modelo.Users;
import com.example.damasfx.VDataBase.DataBase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class ScoreController implements Initializable {
    private static final Logger logger = LogManager.getLogger(ScoreController.class);
    @FXML
    private Label name;

    @FXML
    private Label score;

    @FXML
    private TableView<Users> scoreTable;

    @FXML
    private TableColumn<Users, String> playerColumn;

    @FXML
    private TableColumn<Users, Integer> scoreColumn;

    private UserManagement userCollection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserManagement sessionManagement = new UserManagement();
        userCollection = new UserManagement();  // Inicializar userCollection correctamente
        userCollection.setCurrentUser(userCollection.getUserById(sessionManagement.getLoggedInUser()));

        Users currentUser = userCollection.getCurrentUser();
        name.setText(currentUser.getLogin());
        score.setText(String.valueOf(currentUser.getScores().getScore()));

        playerColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        scoreColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getScores().getScore()).asObject());

        scoreTable.setItems(FXCollections.observableArrayList(userCollection.getUserCollection()));
    }

    @FXML
    public void exit(ActionEvent event){
        logger.info("El usuario ha entrado al menu principal");
        SceneLoader.loadScene("pages/menu-view.fxml",event);
    }
}

