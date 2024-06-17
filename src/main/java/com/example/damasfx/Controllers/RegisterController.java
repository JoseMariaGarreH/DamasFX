package com.example.damasfx.Controllers;

import com.example.damasfx.Enums.ResidenceCountry;
import com.example.damasfx.Enums.RoleType;
import com.example.damasfx.Utils.SceneLoader;
import com.example.damasfx.Utils.UserManagement;
import com.example.damasfx.Models.*;
import com.example.damasfx.Services.DataBase;
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
import java.util.regex.Pattern;


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

    // Lista observable de nacionalidades para el ComboBox
    private ObservableList<ResidenceCountry> nacionalities = FXCollections.observableArrayList(ResidenceCountry.values());

    private UserManagement userCollection = DataBase.getInstance().getUserCollection(); // Gestión de usuarios

    private Properties properties = new Properties(); // Propiedades para cargar configuraciones

    private Pattern emailPattern; // Patrón para validar correos electrónicos

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();
        // Inicializar el patrón de correo electrónico
        emailPattern = Pattern.compile(properties.getProperty("email_pattern"));

        // Deshabilitar la edición del campo de fecha
        inputDate.getEditor().addEventFilter(KeyEvent.ANY, Event::consume);
        // Establecer nacionalidad vacía por defecto y la lista de nacionalidades en el ComboBox
        inputNacionality.setValue(ResidenceCountry.EMPTY);
        inputNacionality.setItems(nacionalities);
        inputNacionality.setVisibleRowCount(3);
    }

    // Método para cargar las propiedades desde un archivo de configuración.
    private void loadProperties() {
        try  {
            InputStream input = RegisterController.class.getClassLoader().getResourceAsStream("general.properties");
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

    @FXML
    public void registered(ActionEvent event) {
        // Restablecer los estilos de los campos
        resetFieldStyles();

        // Obtener y limpiar los datos de los campos de entrada
        String accountText = inputAccount.getText().trim();
        String passwordText = inputPassword.getText().trim();
        String nameText = inputName.getText().trim();
        String surnameText = inputSurname.getText().trim();
        String emailText = inputEmail.getText().trim();
        Date birthDate = convertToDate(inputDate.getValue());
        ResidenceCountry nationality = inputNacionality.getValue();

        // Verificar si hay campos obligatorios vacíos
        if (hasEmptyFields(accountText, passwordText, emailText)) {
            markEmptyFields();
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_empty_fields"));
            logger.warn("No se han rellenado los campos obligatorios en el apartado de registro");
            return;
        }

        // Verificar si el correo electrónico es válido
        if (!emailPattern.matcher(emailText).matches()) {
            labelEmail.setStyle("-fx-text-fill: red");
            inputEmail.setStyle("-fx-border-color: red");
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_incorrect_email"));
            logger.warn("El email o la contraseña no son válidos");
            return;
        }



        // Crear un nuevo usuario con los datos proporcionados
        Users user = new Users();
        user.setLogin(accountText);
        user.setPassword(passwordText);
        user.setName(nameText);
        user.setSurname(surnameText);
        user.setRoleType(RoleType.CLIENTE);
        user.setDateOfBirth(birthDate);
        user.setEmail(emailText);
        user.setNacionality(nationality);
        user.setScore(0);

        // Verificar si el usuario es válido y si no está duplicado
        if (!isUserValid(user)) {
            return;
        }

        // Registrar un mensaje informativo, mostrar alerta y agregar el nuevo usuario
        logger.info("El usuario se ha insertado correctamente");
        showAlert(Alert.AlertType.INFORMATION, "Información", properties.getProperty("added_user_correctly"));
        userCollection.insertNewUser(user);

        // Cargar la siguiente escena (pantalla de inicio)
        SceneLoader.loadScene(properties.getProperty("start_view"), event);
    }

    // Restablece los estilos de los campos a los valores por defecto.
    private void resetFieldStyles() {
        labelAccount.setStyle("-fx-text-fill: black");
        inputAccount.setStyle("-fx-border-color: black");
        labelPassword.setStyle("-fx-text-fill: black");
        inputPassword.setStyle("-fx-border-color: black");
        labelEmail.setStyle("-fx-text-fill: black");
        inputEmail.setStyle("-fx-border-color: black");
    }

    // Marca los campos obligatorios vacíos con un borde rojo.
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

    // Convierte un LocalDate en Date.
    private Date convertToDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    // Verifica si hay campos vacíos.
    private boolean hasEmptyFields(String accountText, String passwordText, String emailText) {
        return accountText.isEmpty() || passwordText.isEmpty() || emailText.isEmpty();
    }

    private boolean isUserValid(Users user) {
        // Verifica si el nombre de usuario ya existe en la colección de usuarios
        if (!userCollection.verifyUser(user)) {
            labelAccount.setStyle("-fx-text-fill: red");
            inputAccount.setStyle("-fx-border-color: red");

            // Muestra una alerta de error con el mensaje que indica que el nombre de usuario ya existe
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_duplicate_user"));

            // Registra una advertencia en el log indicando que el nombre de usuario está duplicado
            logger.warn("El nombre de la cuenta ha sido repetido");

            // Retorna falso indicando que el usuario no es válido
            return false;
        }

        // Verifica si el correo electrónico ya existe en la colección de usuarios
        if (!userCollection.verifyEmail(user)) {
            labelEmail.setStyle("-fx-text-fill: red");
            inputEmail.setStyle("-fx-border-color: red");

            // Muestra una alerta de error con el mensaje que indica que el correo electrónico ya existe
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_duplicate_email"));

            // Registra una advertencia en el log indicando que el correo electrónico está duplicado
            logger.warn("El email ha sido repetido");

            // Retorna falso indicando que el usuario no es válido
            return false;
        }

        // Si el nombre de usuario y el correo electrónico no están duplicados, retorna verdadero indicando que el usuario es válido
        return true;
    }


    // Muestra una alerta
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void comeBack(ActionEvent event) {
        // Comprobamos si el usuario esta logueado, si no lo estuviera entonces lanzaría
        // la escena de inicio de sesión.
        if (userCollection.getUserById(userCollection.getLoggedInUser()) == null) {
            logger.info("El usuario ha vuelto a la pantalla de inicio de sesión");
            SceneLoader.loadScene(properties.getProperty("start_view"), event);
        } else {
            // Si lo estuviera eso significaría que estaría dentro de aplicación y volvería a la escena
            // del segundo inicio de sesión para que el segundo usuario se loguee.
            logger.info("El usuario ha vuelto a la pantalla de inicio de sesión del segundo jugador");
            SceneLoader.loadScene(properties.getProperty("second_user_login"), event);
        }
    }
}
