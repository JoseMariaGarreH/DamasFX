package com.example.damasfx.Controladores;

import com.example.damasfx.Gestion.SceneLoader;
import com.example.damasfx.Gestion.UserManagement;
import com.example.damasfx.Modelo.Users;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
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

    @FXML
    private TableColumn<Users, String> rankingColumn;

    private UserManagement userCollection;
    private Properties properties = new Properties();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();
        UserManagement sessionManagement = new UserManagement();
        userCollection = new UserManagement();  // Inicializar userCollection correctamente
        userCollection.setCurrentUser(userCollection.getUserById(sessionManagement.getLoggedInUser()));

        Users currentUser = userCollection.getCurrentUser();
        name.setText(currentUser.getLogin());
        score.setText(String.valueOf(currentUser.getScore()));

        playerColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        scoreColumn.setSortable(true);

        rankingColumn.setCellValueFactory(cellData -> {
            int rank = scoreTable.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleStringProperty(String.valueOf(rank));
        });

        scoreTable.setItems(FXCollections.observableArrayList(userCollection.getUserCollection()));

        scoreColumn.setSortType(TableColumn.SortType.DESCENDING);
        scoreTable.getSortOrder().add(scoreColumn);
        scoreTable.sort();
    }

    private void loadProperties() {
        try (InputStream input = StartController.class.getClassLoader().getResourceAsStream("general.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

    @FXML
    public void exit(ActionEvent event){
        logger.info("El usuario ha entrado al menu principal");
        SceneLoader.loadScene(properties.getProperty("menu_view"),event);
    }
}

