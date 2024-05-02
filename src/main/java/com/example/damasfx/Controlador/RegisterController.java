package com.example.damasfx.Controlador;

import com.example.damasfx.Vista.DataBase;
import com.example.damasfx.Main;
import com.example.damasfx.Modelo.ResidenceCountry;
import com.example.damasfx.Modelo.User;
import com.example.damasfx.Modelo.UserManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private TextField txtAccount;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtSurname;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPassword;
    @FXML
    private ComboBox<ResidenceCountry> cmbNacionality;
    ObservableList<ResidenceCountry> nacionalities = FXCollections.observableArrayList(ResidenceCountry.values());
    private UserManagement userCollection = DataBase.getInstance().getUserCollection();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.cmbNacionality.setItems(nacionalities);
        this.cmbNacionality.setVisibleRowCount(3);
    }

    @FXML
    public void registered(ActionEvent event) {
        String accountText = this.txtAccount.getText();
        String nameText = this.txtName.getText();
        String surname = this.txtSurname.getText();
        String emailText = this.txtEmail.getText();
        String passwordText = this.txtPassword.getText();
        ResidenceCountry nacionalityValue = this.cmbNacionality.getValue();

        boolean emptyFields = hasEmptyFields(accountText,emailText,nacionalityValue,passwordText);

        if (!emptyFields) {
            showAlert("Error", "Todos los campos son obligatorios");
            return;
        }

        User user = new User(accountText,nameText,surname,passwordText, emailText, nacionalityValue);

        if (!userCollection.verifyUser(user)) {
            showAlert("Error", "Usuario repetido");
            return;
        }

        if(!userCollection.verifyEmail(user)){
            showAlert("Error", "Email no válido");
            return;
        }

        userCollection.insertNewUser(user);
        loadScene("start-view.fxml", event);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean hasEmptyFields(String accountText, String emailText, ResidenceCountry nationalityValue, String passwordText) {
        return !accountText.trim().isEmpty() &&
                !emailText.trim().isEmpty() &&
                !(nationalityValue == null) &&
                !passwordText.trim().isEmpty();
    }

    @FXML
    void comeBack(ActionEvent event) {
        loadScene("start-view.fxml",event);
    }

    public void loadScene(String fxmlPath, ActionEvent event) {
        try{
            Stage ventana = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Scene escena;
            if (ventana.isMaximized()) {
                escena = new Scene(fxmlLoader.load(), ventana.getWidth(), ventana.getHeight());
            } else {
                escena = new Scene(fxmlLoader.load(), 705, 420);
            }
            ventana.setScene(escena);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}

