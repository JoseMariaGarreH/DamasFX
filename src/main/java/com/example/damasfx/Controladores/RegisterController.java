package com.example.damasfx.Controladores;

import com.example.damasfx.Enumerados.ResidenceCountry;
import com.example.damasfx.Enumerados.RoleType;
import com.example.damasfx.Gestion.SceneLoader;
import com.example.damasfx.Gestion.UserManagement;
import com.example.damasfx.Modelo.*;
import com.example.damasfx.VDataBase.DataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    private static final Logger logger = LogManager.getLogger(RegisterController.class);

    @FXML private TextField inputAccount;
    @FXML private DatePicker inputDate;
    @FXML private TextField inputEmail;
    @FXML private ComboBox<ResidenceCountry> inputNacionality;
    @FXML private TextField inputName;
    @FXML private TextField inputPassword;
    @FXML private TextField inputSurname;
    @FXML private Label labelAccount;
    @FXML private Label labelEmail;
    @FXML private Label labelPassword;
    private ObservableList<ResidenceCountry> nacionalities = FXCollections.observableArrayList(ResidenceCountry.values());
    private UserManagement userCollection = DataBase.getInstance().getUserCollection();
    private Properties properties = new Properties();
    private int minPasswordLength;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();
        minPasswordLength = Integer.parseInt(properties.getProperty("min_password_length"));
        inputDate.getEditor().addEventFilter(KeyEvent.ANY, Event::consume);
        inputNacionality.setValue(ResidenceCountry.EMPTY);
        inputNacionality.setItems(nacionalities);
        inputNacionality.setVisibleRowCount(3);
    }

    private void loadProperties() {
        try (InputStream input = RegisterController.class.getClassLoader().getResourceAsStream("general.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

    @FXML
    public void registered(ActionEvent event) {
        resetFieldStyles();

        String accountText = inputAccount.getText().trim();
        String passwordText = inputPassword.getText().trim();
        String nameText = inputName.getText().trim();
        String surnameText = inputSurname.getText().trim();
        String emailText = inputEmail.getText().trim();
        Date birthDate = convertToDate(inputDate.getValue());
        ResidenceCountry nationality = inputNacionality.getValue();

        if (hasEmptyFields(accountText, passwordText, emailText)) {
            markEmptyFields();
            showAlert(Alert.AlertType.ERROR,"Error",properties.getProperty("alert_empty_fields"));
            logger.warn("No se han rellenado los campos obligatorios en el apartado de registro");
            return;
        }

        if (passwordText.length() < minPasswordLength) {
            invalidFields(passwordText);
            showAlert(Alert.AlertType.ERROR,"Error",properties.getProperty("alert_invalid_data"));
            logger.warn("la contraseña no es válida");
            return;
        }

        String encryptedPassword = userCollection.encryptPassword(passwordText);

        Users user = new Users();
        user.setLogin(accountText);
        user.setPassword(encryptedPassword);
        user.setName(nameText);
        user.setSurname(surnameText);
        user.setRoleType(RoleType.CLIENTE);
        user.setDateOfBirth(birthDate);
        user.setEmail(emailText);
        user.setNacionality(nationality);
        user.setScore(0);

        if (!isUserValid(user)) {
            return;
        }

        logger.info("El usuario se ha insertado correctamente");
        showAlert(Alert.AlertType.INFORMATION, "Información",properties.getProperty("added_user_correctly"));
        userCollection.insertNewUser(user);

        // Cargar la siguiente escena
        SceneLoader.loadScene("pages/start-view.fxml", event);
    }

    private void invalidFields(String password) {
        if (password.length() < minPasswordLength) {
            labelPassword.setStyle("-fx-text-fill: red");
            inputPassword.setStyle("-fx-border-color: red");
        }
    }

    private void resetFieldStyles() {
        labelAccount.setStyle("-fx-text-fill: black");
        inputAccount.setStyle("-fx-border-color: black");
        labelPassword.setStyle("-fx-text-fill: black");
        inputPassword.setStyle("-fx-border-color: black");
        labelEmail.setStyle("-fx-text-fill: black");
        inputEmail.setStyle("-fx-border-color: black");
    }

    private void markEmptyFields() {
        if (inputAccount.getText().trim().isEmpty()) {
            labelAccount.setStyle("-fx-text-fill: red");
            inputAccount.setStyle("-fx-border-color: red");
        }

        if (inputPassword.getText().trim().isEmpty()) {
            labelPassword.setStyle("-fx-text-fill: red");
            inputPassword.setStyle("-fx-border-color: red");
        }

        if (inputEmail.getText().trim().isEmpty()) {
            labelEmail.setStyle("-fx-text-fill: red");
            inputEmail.setStyle("-fx-border-color: red");
        }
    }

    private Date convertToDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    private boolean hasEmptyFields(String accountText, String passwordText, String emailText) {
        return accountText.isEmpty() || passwordText.isEmpty() || emailText.isEmpty();
    }

    private boolean isUserValid(Users user) {
        if (!userCollection.verifyUser(user)) {
            labelAccount.setStyle("-fx-text-fill: red");
            inputAccount.setStyle("-fx-border-color: red");
            showAlert(Alert.AlertType.ERROR,"Error",properties.getProperty("alert_duplicate_user"));
            logger.warn("El nombre de la cuenta ha sido repetido");
            return false;
        }

        if (!userCollection.verifyEmail(user)) {
            labelEmail.setStyle("-fx-text-fill: red");
            inputEmail.setStyle("-fx-border-color: red");
            showAlert(Alert.AlertType.ERROR,"Error",properties.getProperty("alert_duplicate_email"));
            logger.warn("El email ha sido repetido");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType,String title ,String content) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void comeBack(ActionEvent event) {
        if(userCollection.getUserById(userCollection.getLoggedInUser()) == null) {
            logger.info("El usuario ha vuelto a la pantalla de inicio de sesión");
            SceneLoader.loadScene("pages/start-view.fxml", event);
        }else{
            logger.info("El usuario ha vuelto a la pantalla de inicio de sesión del segundo jugador");
            SceneLoader.loadScene("pages/secondUserLogin-view.fxml", event);
        }
    }
}
