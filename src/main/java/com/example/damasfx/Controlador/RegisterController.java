package com.example.damasfx.Controlador;

import com.example.damasfx.Modelo.*;
import com.example.damasfx.Vista.DataBase;
import com.example.damasfx.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    private TextField inputAccount;

    @FXML
    private DatePicker inputDate;

    @FXML
    private TextField inputEmail;

    @FXML
    private ComboBox<ResidenceCountry> inputNacionality;

    @FXML
    private TextField inputName;

    @FXML
    private TextField inputPassword;

    @FXML
    private TextField inputSurname;
    @FXML
    private Label labelAccount;

    @FXML
    private Label labelEmail;

    @FXML
    private Label labelPassword;

    ObservableList<ResidenceCountry> nacionalities = FXCollections.observableArrayList(ResidenceCountry.values());
    private UserManagement userCollection = DataBase.getInstance().getUserCollection();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.inputNacionality.setItems(nacionalities);
        this.inputNacionality.setVisibleRowCount(3);
    }

    @FXML
    public void registered(ActionEvent event) {
        String accountText = this.inputAccount.getText();
        String passwordText = this.inputPassword.getText();
        String nameText = this.inputName.getText();
        String surname = this.inputSurname.getText();
        String emailText = this.inputEmail.getText();
        LocalDate localDate = this.inputDate.getValue();
        Date date;
        if(localDate != null) {
            Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
            date = Date.from(instant);
        }else{
            date = new Date();
        }
        ResidenceCountry nacionalityValue = this.inputNacionality.getValue();

        boolean emptyFields = hasEmptyFields(accountText,passwordText,emailText);

        if (!emptyFields) {
            this.labelAccount.setText("Login");
            this.labelPassword.setText("Contraseña");
            this.labelEmail.setText("Email");

            this.labelAccount.setStyle("-fx-text-fill: red");
            this.labelPassword.setStyle("-fx-text-fill: red");
            this.labelEmail.setStyle("-fx-text-fill: red");

            this.inputAccount.setStyle("-fx-border-color: red");
            this.inputPassword.setStyle("-fx-border-color: red");
            this.inputEmail.setStyle("-fx-border-color: red");

            showAlert("Rellena los campos son obligatorios");
            return;
        }

        User user = new User(accountText,nameText,surname,RoleType.CLIENTE,date,passwordText,emailText,nacionalityValue);

        if (!userCollection.verifyUser(user)) {
            showAlert("Usuario repetido");
            return;
        }

        if(!userCollection.verifyEmail(user)){
            showAlert("Email no válido");
            return;
        }

        userCollection.insertNewUser(user);
        SceneLoader.loadScene("start-view.fxml", event);
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean hasEmptyFields(String accountText, String passwordText,String emailText) {
        return !accountText.trim().isEmpty() &&
                !emailText.trim().isEmpty() &&
                !passwordText.trim().isEmpty();
    }

    @FXML
    void comeBack(ActionEvent event) {
        SceneLoader.loadScene("start-view.fxml",event);
    }
}

