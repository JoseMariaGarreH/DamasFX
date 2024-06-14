package com.example.damasfx.Controllers;

import com.example.damasfx.Enums.ActionType;
import com.example.damasfx.Enums.ResidenceCountry;
import com.example.damasfx.Enums.RoleType;
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

    private ObservableList<ResidenceCountry> listOfCountries = FXCollections.observableArrayList(ResidenceCountry.values()); // Lista observable de países de residencia utilizando los valores del enum ResidenceCountry
    private ObservableList<RoleType> listOfRoles = FXCollections.observableArrayList(RoleType.values()); // Lista observable de roles utilizando los valores del enum RoleType
    private final UserManagement userCollection = DataBase.getInstance().getUserCollection(); // Instancia de UserManagement obtenida de la base de datos
    private Users currentUser; // Usuario actual que se está manejando
    private Users originalUser; // Usuario original para comparación de cambios
    private ActionType actionType; // Tipo de acción que se va a realizar (GUARDAR, MODIFICAR)
    private Pattern emailPattern; // Patrón para validar direcciones de correo electrónico
    private Properties properties = new Properties(); // Propiedades cargadas desde un archivo de configuración

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadProperties();
        // Compila el patrón de correo electrónico utilizando la propiedad cargada
        emailPattern = Pattern.compile(properties.getProperty("email_pattern"));

        // Agrega un filtro de eventos para evitar que el usuario edite directamente el editor de fecha
        inputDate.getEditor().addEventFilter(KeyEvent.ANY, Event::consume);

        // Inicialmente, el botón de cambio no es visible
        btnChange.setVisible(false);
        // Establece el valor inicial del cuadro de selección de nacionalidad a un valor vacío
        boxNacionality.setValue(ResidenceCountry.EMPTY);
        // Establece los ítems del cuadro de selección de nacionalidad con la lista de países
        boxNacionality.setItems(listOfCountries);
        // Establece la cantidad de filas visibles del cuadro de selección de nacionalidad
        boxNacionality.setVisibleRowCount(3);

        // Establece los ítems del cuadro de selección de roles con la lista de roles
        boxRole.setItems(listOfRoles);
    }


    // Método para cargar las propiedades desde un archivo de configuración.
    private void loadProperties() {
        try (InputStream input = WindowController.class.getClassLoader().getResourceAsStream("general.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

    public void initAttributes(Users user) {
        // Asigna el usuario proporcionado al atributo currentUser
        this.currentUser = user;

        // Si el usuario no es null, procede a inicializar los detalles del usuario,
        // clonar el usuario original y ajustar la visibilidad basada en el tipo de acción
        if (user != null) {
            // Rellena los detalles del usuario en los campos correspondientes de la interfaz
            populateUserDetails(user);
            // Clona el usuario original para poder comparar cambios más tarde
            cloneOriginalUser(user);
            // Ajusta la visibilidad de ciertos elementos de la interfaz basádo en el tipo de acción actual
            adjustVisibilityBasedOnActionType();
        }
    }

    // Método para rellenar el usuario a modificar los detalles del usuario en los campos de la interfaz
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
        // Si la fecha no es null, la convierte a LocalDate usando la zona horaria del sistema,
        // de lo contrario, devuelve la fecha actual
        return (date != null) ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : LocalDate.now();
    }

    private void cloneOriginalUser(Users user) {
        try {
            // Intenta clonar el usuario y asignarlo a originalUser
            originalUser = (Users) user.clone();
        } catch (CloneNotSupportedException e) {
            // Si ocurre un error al clonar, lanza una RuntimeException
            logger.error("Error al clonar el usuario seleccionado: "+e);
        }
    }

    private void adjustVisibilityBasedOnActionType() {
        // Si el tipo de acción es GUARDAR, oculta el botón de cambiar
        if (actionType == ActionType.GUARDAR) {
            btnChange.setVisible(false);
        }
        // Si el tipo de acción es MODIFICAR, ajusta la visibilidad y la habilitación
        // de los campos basádo en el rol del usuario actual
        else if (actionType == ActionType.MODIFICAR) {
            if (userCollection.getCurrentUser().getRoleType() == RoleType.ADMINISTRADOR) {
                // Si el rol es ADMINISTRADOR, deshabilita ciertos campos y oculta el botón de cambiar
                inputPassword.setDisable(true);
                inputAccount.setDisable(true);
                inputEmail.setDisable(true);
                btnChange.setVisible(false);
            } else if (userCollection.getCurrentUser().getRoleType() == RoleType.CREADOR) {
                // Si el rol es CREADOR, deshabilita la contraseña, pero habilita la cuenta y el email,
                // y muestra el botón de cambiar
                inputPassword.setDisable(true);
                inputAccount.setDisable(false);
                inputEmail.setDisable(false);
                btnChange.setVisible(true);
            }
        }
        // Si no es ninguno de los anteriores, muestra el botón de cambiar
        else {
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
        // Obtiene y recorta el texto de los campos de entrada de la interfaz
        String login = inputAccount.getText().trim();
        String password = inputPassword.getText().trim();
        RoleType role = boxRole.getValue();
        String name = inputName.getText().trim();
        String surname = inputSurname.getText().trim();
        LocalDate localDate = inputDate.getValue();
        Date date = convertToDate(localDate);
        String email = inputEmail.getText().trim();
        ResidenceCountry country = boxNacionality.getValue();

        // Verifica si hay campos vacíos obligatorios
        if (hasEmptyFields(login, email, password)) {
            // Resalta los campos vacíos y muestra una alerta de error
            modifyStyle();
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_empty_fields"));
            logger.warn("No se han rellenado los campos obligatorios en el apartado de registro");
            return;
        }

        // Verifica si el email tiene un formato válido
        if (!emailPattern.matcher(email).matches()) {
            // Resalta el campo de email con errores y muestra una alerta de error
            labelEmail.setStyle("-fx-text-fill: red");
            inputEmail.setStyle("-fx-border-color: red");
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_incorrect_email"));
            logger.warn("El email o la contraseña no son válidos");
            return;
        }

        // Restaura los estilos de los campos después de la validación
        resetFieldStyles();

        // Si currentUser no es null, se actualizan los detalles del usuario existente
        if (currentUser != null) {
            // Intenta actualizar los detalles del usuario y muestra una alerta informativa si es exitoso
            if (updateUser(login, password, role, name, surname, date, email, country)) {
                showAlert(Alert.AlertType.INFORMATION, "Información", properties.getProperty("modified_user_correctly"));
                logger.info("Se han modificado correctamente los datos del usuario");
                closeWindow(); // Cierra la ventana después de actualizar el usuario
            }
        } else {
            // Si currentUser es null, se crea un nuevo usuario
            if (createUser(login, password, role, name, surname, date, email, country)) {
                showAlert(Alert.AlertType.INFORMATION, "Información", properties.getProperty("added_user_correctly"));
                logger.info("Se ha insertado al usuario correctamente");
                closeWindow(); // Cierra la ventana después de crear el usuario
            }
        }
    }

    // Método para actualizar los detalles del usuario existente
    private boolean updateUser(String login, String password, RoleType role, String name, String surname, Date date, String email, ResidenceCountry country) {
        // Asigna los nuevos valores al usuario actual
        currentUser.setLogin(login);
        currentUser.setPassword(password);
        currentUser.setRoleType(role);
        currentUser.setEmail(email);
        currentUser.setName(name);
        currentUser.setSurname(surname);
        currentUser.setDateOfBirth(date);
        currentUser.setNacionality(country);

        // Verifica si el login ha cambiado y si el nuevo login ya existe en la colección
        if (!originalUser.getLogin().trim().equalsIgnoreCase(currentUser.getLogin())) {
            if (!userCollection.verifyUser(currentUser)) {
                // Resalta el campo de login con errores y muestra una alerta de error
                labelAccount.setStyle("-fx-text-fill: red");
                inputAccount.setStyle("-fx-border-color: red");
                showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_duplicate_user"));
                logger.warn("El nombre de la cuenta que ha introducido el usuario ya existe");
                return false;
            }
            // Verifica si el email ha cambiado y si el nuevo email ya existe en la colección
        } else if (!originalUser.getEmail().trim().equalsIgnoreCase(currentUser.getEmail())) {
            if (!userCollection.verifyEmail(currentUser)) {
                // Resalta el campo de email con errores y muestra una alerta de error
                labelEmail.setStyle("-fx-text-fill: red");
                inputEmail.setStyle("-fx-border-color: red");
                showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_duplicate_email"));
                logger.warn("El email introducido por el usuario ya existe");
                return false;
            }
        }

        // Devuelve true si los detalles del usuario han cambiado, de lo contrario false
        return !originalUser.equals(currentUser);
    }

    private boolean checkUserEmail(Users user) {
        boolean hasError = false;
        // Verifica si el usuario ya existe en la colección
        if (!userCollection.verifyUser(user)) {
            // Resalta el campo de login con errores y muestra una alerta de error
            labelAccount.setStyle("-fx-text-fill: red");
            inputAccount.setStyle("-fx-border-color: red");
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_duplicate_user"));
            logger.warn("El nombre de la cuenta que ha introducido el usuario ya existe");
            hasError = true;
        }
        // Verifica si el email ya existe en la colección
        if (!userCollection.verifyEmail(user)) {
            // Resalta el campo de email con errores y muestra una alerta de error
            labelEmail.setStyle("-fx-text-fill: red");
            inputEmail.setStyle("-fx-border-color: red");
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_duplicate_email"));
            logger.warn("El email introducido por el usuario ya existe");
            hasError = true;
        }
        return !hasError;
    }

    private boolean createUser(String login, String password, RoleType role, String name, String surname, Date date, String email, ResidenceCountry country) {
        // Crea una nueva instancia de usuario y asigna los valores proporcionados
        Users newUser = new Users();

        newUser.setLogin(login);
        newUser.setPassword(password);
        newUser.setName(name);
        newUser.setSurname(surname);
        newUser.setRoleType(role != null ? role : RoleType.CLIENTE);
        newUser.setDateOfBirth(date);
        newUser.setEmail(email);
        newUser.setNacionality(country);
        newUser.setScore(0);

        // Verifica si el usuario o el email ya existen en la colección
        if (checkUserEmail(newUser)) {
            currentUser = newUser; // Asigna el nuevo usuario a currentUser si no hay errores
            return true;
        }
        return false;
    }

    @FXML
    private void resetPassword(ActionEvent event) {
        // Habilita el campo de entrada de contraseña
        inputPassword.setDisable(false);
        // Borra el texto del campo de contraseña
        inputPassword.setText(null);
    }

    private Date convertToDate(LocalDate localDate) {
        // Si localDate no es null, convierte a Date usando la zona horaria del sistema
        // De lo contrario, devuelve la fecha actual
        return (localDate != null) ? Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : new Date();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        // Crea una nueva alerta con el tipo especificado
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null); // Elimina el encabezado
        alert.setTitle(title); // Establece el título de la alerta
        alert.setContentText(content); // Establece el contenido de la alerta
        alert.showAndWait(); // Muestra la alerta y espera a que el usuario la cierre
    }

    @FXML
    private void exit(ActionEvent event) {
        // Restablece currentUser a null
        currentUser = null;
        // Cierra la ventana actual
        closeWindow();
    }

    private boolean hasEmptyFields(String login, String email, String password) {
        // Devuelve true si alguno de los campos está vacío, de lo contrario false
        return login.isEmpty() || email.isEmpty() || password.isEmpty();
    }

    private void modifyStyle() {
        // Verifica si el campo de cuenta está vacío
        if (inputAccount.getText().trim().isEmpty()) {
            // Si está vacío, cambia el color del texto del label a rojo y el borde del campo a rojo
            labelAccount.setStyle("-fx-text-fill: red");
            inputAccount.setStyle("-fx-border-color: red");
        } else {
            // Si no está vacío, cambia el color del texto del label a negro y el borde del campo a negro
            labelAccount.setStyle("-fx-text-fill: black");
            inputAccount.setStyle("-fx-border-width: 3px; -fx-border-color: black; -fx-border-radius: 2px;");
        }

        // Verifica si el campo de contraseña está vacío
        if (inputPassword.getText().trim().isEmpty()) {
            // Si está vacío, cambia el color del texto del label a rojo y el borde del campo a rojo
            labelPassword.setStyle("-fx-text-fill: red");
            inputPassword.setStyle("-fx-border-color: red");
        } else {
            // Si no está vacío, cambia el color del texto del label a negro y el borde del campo a negro
            labelPassword.setStyle("-fx-text-fill: black");
            inputPassword.setStyle("-fx-border-width: 3px; -fx-border-color: black; -fx-border-radius: 2px;");
        }

        // Verifica si el campo de email está vacío
        if (inputEmail.getText().trim().isEmpty()) {
            // Si está vacío, cambia el color del texto del label a rojo y el borde del campo a rojo
            labelEmail.setStyle("-fx-text-fill: red");
            inputEmail.setStyle("-fx-border-color: red");
        } else {
            // Si no está vacío, cambia el color del texto del label a negro y el borde del campo a negro
            labelEmail.setStyle("-fx-text-fill: black");
            inputEmail.setStyle("-fx-border-width: 3px; -fx-border-color: black; -fx-border-radius: 2px;");
        }
    }

    public Users getCurrentUser(){
        return currentUser;
    }

    private void closeWindow() {
        Stage stage = (Stage) btnEnter.getScene().getWindow();
        stage.close();
    }

}
