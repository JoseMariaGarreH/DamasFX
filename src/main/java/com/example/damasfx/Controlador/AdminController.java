package com.example.damasfx.Controlador;

import com.example.damasfx.Main;
import com.example.damasfx.Modelo.SceneLoader;
import com.example.damasfx.Modelo.User;
import com.example.damasfx.Modelo.UserManagement;
import com.example.damasfx.Vista.DataBase;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    @FXML
    private Label outputName;
    @FXML
    private TableColumn<User, String> loginColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> nacionalityColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> surnameColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, LocalDate> dateColumn;
    @FXML
    private TableView<User> userTable;
    private UserManagement userCollection = DataBase.getInstance().getUserCollection();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        outputName.setText(userCollection.getCurrentUser().getName());

        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleType"));
        nacionalityColumn.setCellValueFactory(new PropertyValueFactory<>("nacionality"));

        userTable.setItems(userCollection.getUserCollection());
    }

    @FXML
    private void handlerAdd(ActionEvent event) {
        try {
            // Cargo la vista
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("window-view.fxml"));

            // Creo el Scene
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            WindowController windowController = loader.getController();

            // cojo la persona devuelta
            User u = windowController.getUser();
            if (u != null) {
                // Añado la persona
                boolean isInserted = userCollection.insertNewUser(u);
                if (isInserted) {
                    ObservableList<User> data = userTable.getItems();
                    data.add(u);
                }
            }

        } catch (IOException ex) {
            showAlert(Alert.AlertType.INFORMATION,"Error",ex.getMessage());
        }
    }

    @FXML
    public void handlerModify(ActionEvent event) {
        User u = userTable.getSelectionModel().getSelectedItem();

        if (u != null) {
            try {
                // Cargo la vista
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("window-view.fxml"));

                // Cargo la ventana
                Parent root = loader.load();

                // Cojo el controlador
                WindowController windowController = loader.getController();
                windowController.initAttributtes(u);

                // Creo el Scene
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();

                // Cojo la persona devuelta
                User aux = windowController.getUser();
                if(aux != null){
                    boolean isModified = userCollection.modifyUser(aux);
                    if (isModified) {
                        ObservableList<User> data = userTable.getItems();
                        int index = data.indexOf(u);
                        if (index != -1) {
                            data.set(index, aux);
                        }
                        userTable.getSelectionModel().clearSelection();
                    }else{
                        showAlert(Alert.AlertType.ERROR,"Error","El usuario ya existe");
                    }
                }
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR,"Error",ex.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR,"Error","Debes seleccionar una persona");
        }
    }

    @FXML
    public void handlerDelete(ActionEvent event) {
        User u = userTable.getSelectionModel().getSelectedItem();

        if (u != null && userCollection.deleteUser(u)) {
            // Elimino la persona
            boolean isDeleted = userCollection.deleteUser(u);
            if (isDeleted) {
                ObservableList<User> data = userTable.getItems();
                data.remove(u);
                userTable.getSelectionModel().clearSelection();

                showAlert(Alert.AlertType.INFORMATION,"Info","Se ha borrado la persona");
            }
        } else {
            showAlert(Alert.AlertType.ERROR,"Error","Debes seleccionar una persona");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void goToMenu(ActionEvent event){
        SceneLoader.loadScene("menu-view.fxml",event);
    }

    @FXML
    void exit(ActionEvent event) {
        SceneLoader.loadScene("start-view.fxml", event);
    }
}

