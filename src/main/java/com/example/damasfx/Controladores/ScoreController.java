package com.example.damasfx.Controladores;

import com.example.damasfx.Gestion.SceneLoader;
import com.example.damasfx.Modelo.Scores;
import com.example.damasfx.Gestion.ScoresManagement;
import com.example.damasfx.Gestion.UserManagement;
import com.example.damasfx.VDataBase.DataBase;
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
    private TableColumn<Scores, Integer> scoreColumn;
    @FXML
    private TableColumn<Scores, String> playerColumn;
    @FXML
    private TableView<Scores> scoreTable;
    @FXML
    private Label name;
    @FXML
    private Label score;
    private ScoresManagement scoreCollection = DataBase.getInstance().getScoreCollection();
    private UserManagement userCollection = DataBase.getInstance().getUserCollection();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserManagement sesionManagement = new UserManagement();
        userCollection.setCurrentUser(userCollection.getUserById(sesionManagement.getLoggedInUser()));
        name.setText(userCollection.getCurrentUser().getLogin());
        score.setText(String.valueOf(userCollection.getCurrentUser().getScores().getScore()));

        playerColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        scoreTable.setItems(scoreCollection.getScoreCollection());
    }

    @FXML
    public void exit(ActionEvent event){
        logger.info("El usuario ha entrado al menu principal");
        SceneLoader.loadScene("pages/menu-view.fxml",event);
    }
}

