package com.example.damasfx.Controllers;

import com.example.damasfx.Utils.SceneLoader;
import com.example.damasfx.Utils.UserManagement;
import com.example.damasfx.Models.Users;
import com.example.damasfx.Services.DataBase;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.*;

public class SecondUserController implements Initializable {
    private static final Logger logger = LogManager.getLogger(StartController.class);

    @FXML private ToggleButton togglePassword;
    @FXML private ImageView image;
    @FXML private TextField txtAccount;
    @FXML private PasswordField txtPasswordNotVisible;
    @FXML private TextField txtPasswordVisible;

    private UserManagement userCollection = DataBase.getInstance().getUserCollection(); // Instancia de UserManagement para la gestión de usuarios
    private Properties properties = new Properties(); // Propiedades cargadas desde un archivo de configuración

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();
        // Cargamos al primer usuario
        UserManagement sesionManagement = new UserManagement();
        userCollection.setFirstUser(userCollection.getUserById(sesionManagement.getLoggedInUser()));
        // Introducimos eventos para la tecla enter en los siguientes campos
        txtAccount.setOnKeyPressed(this::handleEnterKey);
        txtPasswordVisible.setOnKeyPressed(this::handleEnterKey);
        txtPasswordNotVisible.setOnKeyPressed(this::handleEnterKey);
    }

    // Método para cargar las propiedades desde un archivo de configuración
    private void loadProperties() {
        try {
            InputStream input = SecondUserController.class.getClassLoader().getResourceAsStream("general.properties");
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

    // Manejador de eventos para detectar la tecla Enter y llamar al método de inicio de sesión
    private void handleEnterKey(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == ENTER) {
            logIn(event);
            event.consume();
        }
    }


    @FXML
    public void logIn(Event event) {
        // Obtiene el texto ingresado en los campos de cuenta y contraseña
        String account = txtAccount.getText();
        String password = !txtPasswordVisible.getText().trim().isEmpty() ? txtPasswordVisible.getText().trim() : txtPasswordNotVisible.getText().trim();
        // Intenta iniciar sesión con los datos proporcionados
        Users secondUser = userCollection.userLogin(account, password);

        // Si el usuario existe, procede con el inicio de sesión
        if (secondUser != null) {
            // Luego comprobamos que el usuario que intenta
            // loguearse por segunda vez, no sea el mismo que el que se logueo por primera vez
            Users currentUser = userCollection.getFirstUser();
            if (currentUser != null && currentUser.getLogin().equals(secondUser.getLogin())) { // Si es así
                logger.warn("El usuario que intenta iniciar sesión es el mismo que el usuario actual");
                // Se le muestra un mensaje de error
                showAlert("Error", "No puedes iniciar sesión con el mismo usuario que ya está logueado.");
            } else { //Si no
                //Guardamos al usuario como segundo usuario
                logger.info("El usuario se ha introducido correctamente a la aplicación");
                userCollection.setSecondUser(secondUser);

                logger.info("El usuario ha entrado a la zona de administración correctamente");
                SceneLoader.loadScene(properties.getProperty("play_view"), event);
            }
        } else {
            // Si las credenciales son incorrectas, muestra una alerta y registra un aviso
            logger.warn("El nombre de la cuenta o la contraseña introducidas por el usuario son incorrectas");
            showAlert("Error", properties.getProperty("incorrect_current_data"));
        }
    }

    @FXML
    public void forgotPassword(MouseEvent event) {
        logger.info("El usuario se ha dirigido a la escena de recuperación de contraseña");
        SceneLoader.loadScene(properties.getProperty("search_email_view"), event);
    }

    // Método para mostrar una alerta con el mensaje de error
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Método para manejar el evento de navegación a la escena de registro
    @FXML
    void registerScene(MouseEvent event) {
        logger.info("El usuario se ha dirigido a la zona de registro");
        SceneLoader.loadScene(properties.getProperty("register_view"), event);
    }


    @FXML
    public void showPassword(ActionEvent event) {
        try {
            // Cambia la visibilidad de la contraseña según el estado del ToggleButton
            if (togglePassword.isSelected()) {
                setPasswordVisibility(true, properties.getProperty("image_closed"));
            } else {
                setPasswordVisibility(false, properties.getProperty("image_open"));
            }
        } catch (FileNotFoundException e) {
            logger.error("Las imagenes no se ha encontrado correctamente y por lo tanto han fallado", e);
        }
    }

    // Método para establecer la visibilidad de la contraseña y cambiar la imagen del botón
    private void setPasswordVisibility(boolean visible, String imgPath) throws FileNotFoundException {
        File imgFile = new File(imgPath);
        if (!imgFile.exists()) {
            logger.error("El archivo de imagen no se encontró: " + imgPath);
            return;
        }
        InputStream imgStream = new FileInputStream(imgFile);
        image.setImage(new Image(imgStream));

        if (visible) {
            txtPasswordVisible.setText(txtPasswordNotVisible.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordNotVisible.setVisible(false);
        } else {
            txtPasswordNotVisible.setText(txtPasswordVisible.getText());
            txtPasswordVisible.setVisible(false);
            txtPasswordNotVisible.setVisible(true);
        }
    }

    // Método para manejar el evento de salida de la aplicación
    @FXML
    public void comeBack(ActionEvent event) {
        logger.info("El usuario se ha dirigido a la zona de registro");
        SceneLoader.loadScene(properties.getProperty("menu_view"), event);
    }
}
