package com.example.damasfx.Controladores;

import com.example.damasfx.Enumerados.ActionType;
import com.example.damasfx.Enumerados.ResidenceCountry;
import com.example.damasfx.Enumerados.RoleType;
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
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class WindowController implements Initializable {
    private static final Logger logger = LogManager.getLogger(WindowController.class);

    @FXML private Button btnEnter;
    @FXML private ComboBox<ResidenceCountry> boxNacionality;
    @FXML private TextField inputSurname;
    @FXML private DatePicker inputDate;
    @FXML private TextField inputAccount;
    @FXML private TextField inputPassword;
    @FXML private TextField inputName;
    @FXML private TextField inputEmail;
    @FXML private ComboBox<RoleType> boxRole;
    @FXML private Label labelAccount;
    @FXML private Label labelEmail;
    @FXML private Label labelPassword;
    @FXML private ToggleButton btnChange;
    private ObservableList<ResidenceCountry> listOfCountries = FXCollections.observableArrayList(ResidenceCountry.values());
    private ObservableList<RoleType> listOfRoles = FXCollections.observableArrayList(RoleType.values());
    private final UserManagement userCollection = DataBase.getInstance().getUserCollection();
    private Users currentUser;
    private Users originalUser;
    private ActionType actionType;
    private Pattern emailPattern;
    private Properties properties = new Properties();

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadProperties();
        emailPattern = Pattern.compile(properties.getProperty("email_pattern"));

        inputDate.getEditor().addEventFilter(KeyEvent.ANY, Event::consume);

        btnChange.setVisible(false);
        boxNacionality.setValue(ResidenceCountry.EMPTY);
        boxNacionality.setItems(listOfCountries);
        boxNacionality.setVisibleRowCount(3);

        boxRole.setItems(listOfRoles);
    }

    private void loadProperties() {
        try (InputStream input = WindowController.class.getClassLoader().getResourceAsStream("general.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

    public void initAttributes(Users user) {
        this.currentUser = user;
        if (user != null) {
            populateUserDetails(user);
            cloneOriginalUser(user);
            adjustVisibilityBasedOnActionType();
        }
    }

    private void populateUserDetails(Users user) {
        inputAccount.setText(user.getLogin());
        inputPassword.setText(user.getPassword());
        boxRole.setValue(user.getRoleType());
        inputName.setText(user.getName());
        inputSurname.setText(user.getSurname());
        inputDate.setValue(convertToLocalDate(user.getDateOfBirth()));
        inputEmail.setText(user.getEmail());
        boxNacionality.setValue(user.getNacionality());
    }

    private LocalDate convertToLocalDate(Date date) {
        return (date != null) ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : LocalDate.now();
    }

    private void cloneOriginalUser(Users user) {
        try {
            originalUser = (Users) user.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private void adjustVisibilityBasedOnActionType() {
        if (actionType == ActionType.GUARDAR) {
            btnChange.setVisible(false);
        } else if (actionType == ActionType.MODIFICAR) {
            if (userCollection.getCurrentUser().getRoleType() == RoleType.ADMINISTRADOR){
                inputPassword.setDisable(true);
                inputAccount.setDisable(true);
                inputEmail.setDisable(true);
                btnChange.setVisible(false);
            }else if(userCollection.getCurrentUser().getRoleType() == RoleType.CREADOR){
                inputPassword.setDisable(true);
                inputAccount.setDisable(false);
                inputEmail.setDisable(false);
                btnChange.setVisible(true);
            }
        } else {
            btnChange.setVisible(true);
        }
    }

    private void resetFieldStyles() {
        // Resetea estilos del login
        labelAccount.setStyle("-fx-text-fill: black");
        inputAccount.setStyle("-fx-border-width: 3px; -fx-border-color: black; -fx-border-radius: 2px;");

        // Resetea estilos de la contraseña
        labelPassword.setStyle("-fx-text-fill: black");
        inputPassword.setStyle("-fx-border-width: 3px; -fx-border-color: black; -fx-border-radius: 2px;");

        // Resetea estilos del email
        labelEmail.setStyle("-fx-text-fill: black");
        inputEmail.setStyle("-fx-border-width: 3px; -fx-border-color: black; -fx-border-radius: 2px;");
    }

    @FXML
    private void save(ActionEvent event) {
        String login = inputAccount.getText().trim();
        String password = inputPassword.getText().trim();
        RoleType role = boxRole.getValue();
        String name = inputName.getText().trim();
        String surname = inputSurname.getText().trim();
        LocalDate localDate = inputDate.getValue();
        Date date = convertToDate(localDate);
        String email = inputEmail.getText().trim();
        ResidenceCountry country = boxNacionality.getValue();

        if (hasEmptyFields(login, email, password)) {
            highlightEmptyFields();
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_empty_fields"));
            logger.warn("No se han rellenado los campos obligatorios en el apartado de registro");
            return;
        }

        if (!emailPattern.matcher(email).matches()) {
            labelEmail.setStyle("-fx-text-fill: red");
            inputEmail.setStyle("-fx-border-color: red");
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_incorrect_email"));
            logger.warn("El email o la contraseña no son válidos");
            return;
        }

        resetFieldStyles();

        if (currentUser != null) {
            if (updateUserDetails(login, password, role, name, surname, date, email, country)) {
                showAlert(Alert.AlertType.INFORMATION, "Información", properties.getProperty("modified_user_correctly"));
                logger.info("Se han modificado correctamente los datos del usuario");
                closeWindow();
            }
        } else {
            if (createUser(login, password, role, name, surname, date, email, country)) {
                showAlert(Alert.AlertType.INFORMATION, "Información", properties.getProperty("added_user_correctly"));
                logger.info("Se ha insertado al usuario correctamente");
                closeWindow();
            }
        }
    }

    private boolean updateUserDetails(String login, String password, RoleType role, String name, String surname, Date date, String email, ResidenceCountry country) {
        currentUser.setLogin(login);
        currentUser.setPassword(currentUser.getPassword().equals(password) ? password : userCollection.encryptPassword(password));
        currentUser.setRoleType(role);
        currentUser.setEmail(email);
        currentUser.setName(name);
        currentUser.setSurname(surname);
        currentUser.setDateOfBirth(date);
        currentUser.setNacionality(country);

        if(!originalUser.getLogin().trim().equalsIgnoreCase(currentUser.getLogin())) {
            if (!userCollection.verifyUser(currentUser)) {
                labelAccount.setStyle("-fx-text-fill: red");
                inputAccount.setStyle("-fx-border-color: red");
                showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_duplicate_user"));
                logger.warn("El nombre de la cuenta que ha introducido el usuario ya existe");
                return false;
            }
        } else if (!originalUser.getEmail().trim().equalsIgnoreCase(currentUser.getEmail())) {
            if (!userCollection.verifyEmail(currentUser)) {
                labelEmail.setStyle("-fx-text-fill: red");
                inputEmail.setStyle("-fx-border-color: red");
                showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_duplicate_email"));
                logger.warn("El email introducido por el usuario ya existe");
                return false;
            }
        }

        return !originalUser.equals(currentUser);
    }

    private boolean checkUserEmail(Users user) {
        boolean hasError = false;
        if (!userCollection.verifyUser(user)) {
            labelAccount.setStyle("-fx-text-fill: red");
            inputAccount.setStyle("-fx-border-color: red");
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_duplicate_user"));
            logger.warn("El nombre de la cuenta que ha introducido el usuario ya existe");
            hasError = true;
        }
        if (!userCollection.verifyEmail(user)) {
            labelEmail.setStyle("-fx-text-fill: red");
            inputEmail.setStyle("-fx-border-color: red");
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_duplicate_email"));
            logger.warn("El email introducido por el usuario ya existe");
            hasError = true;
        }
        return !hasError;
    }

    private boolean createUser(String login, String password, RoleType role, String name, String surname, Date date, String email, ResidenceCountry country) {
        Users newUser = new Users();

        newUser.setLogin(login);
        newUser.setPassword(userCollection.encryptPassword(password));
        newUser.setName(name);
        newUser.setSurname(surname);
        newUser.setRoleType(role != null ? role : RoleType.CLIENTE);
        newUser.setDateOfBirth(date);
        newUser.setEmail(email);
        newUser.setNacionality(country);
        newUser.setScore(0);

        if (checkUserEmail(newUser)) {
            currentUser = newUser;
            return true;
        }
        return false;
    }

    @FXML
    private void resetPassword(ActionEvent event) {
        inputPassword.setDisable(false);
        inputPassword.setText(null);
    }

    private Date convertToDate(LocalDate localDate) {
        return (localDate != null) ? Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : new Date();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void exit(ActionEvent event) {
        currentUser = null;
        closeWindow();
    }

    private boolean hasEmptyFields(String login, String email, String password) {
        return login.isEmpty() || email.isEmpty() || password.isEmpty();
    }

    private void highlightEmptyFields() {
        if (inputAccount.getText().trim().isEmpty()) {
            labelAccount.setStyle("-fx-text-fill: red");
            inputAccount.setStyle("-fx-border-color: red");
        } else {
            labelAccount.setStyle("-fx-text-fill: black");
            inputAccount.setStyle("-fx-border-width: 3px; -fx-border-color: black; -fx-border-radius: 2px;");
        }

        if (inputPassword.getText().trim().isEmpty()) {
            labelPassword.setStyle("-fx-text-fill: red");
            inputPassword.setStyle("-fx-border-color: red");
        } else {
            labelPassword.setStyle("-fx-text-fill: black");
            inputPassword.setStyle("-fx-border-width: 3px; -fx-border-color: black; -fx-border-radius: 2px;");
        }

        if (inputEmail.getText().trim().isEmpty()) {
            labelEmail.setStyle("-fx-text-fill: red");
            inputEmail.setStyle("-fx-border-color: red");
        } else {
            labelEmail.setStyle("-fx-text-fill: black");
            inputEmail.setStyle("-fx-border-width: 3px; -fx-border-color: black; -fx-border-radius: 2px;");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) btnEnter.getScene().getWindow();
        stage.close();
    }

    public Users getCurrentUser() {
        return currentUser;
    }
}
