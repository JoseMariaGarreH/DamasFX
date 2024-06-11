package com.example.damasfx.Controladores;

import com.example.damasfx.Gestion.SceneLoader;
import com.example.damasfx.VDataBase.DataBase;
import com.example.damasfx.Modelo.Users;
import com.example.damasfx.Gestion.UserManagement;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class StartController implements Initializable {
    private static final Logger logger = LogManager.getLogger(StartController.class);

    @FXML private ToggleButton chkPassword;
    @FXML private TextField txtAccount;
    @FXML private TextField txtPasswordVisible;
    @FXML private ImageView image;
    @FXML private PasswordField txtPasswordNotVisible;
    @FXML private Button btnExit;
    private UserManagement userCollection = DataBase.getInstance().getUserCollection();
    private Properties properties = new Properties();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();
        txtAccount.setOnKeyPressed(this::handleEnterKey);
        txtPasswordVisible.setOnKeyPressed(this::handleEnterKey);
        txtPasswordNotVisible.setOnKeyPressed(this::handleEnterKey);
    }

    private void loadProperties() {
        try (InputStream input = StartController.class.getClassLoader().getResourceAsStream("general.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

    private void handleEnterKey(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            logIn(event);
            event.consume();
        }
    }

    @FXML
    public void logIn(Event event) {
        String account = txtAccount.getText();
        String password = !txtPasswordVisible.getText().trim().isEmpty() ? txtPasswordVisible.getText().trim() : txtPasswordNotVisible.getText().trim();
        Users firtsUser = userCollection.userLogin(account, password);

        if (firtsUser != null) {
            logger.info("El usuario se ha introducido correctamente a la aplicación");
            userCollection.setCurrentUser(firtsUser);
            userCollection.saveLoggedInUser(firtsUser.getLogin());
            userCollection.setFirstUser(firtsUser);

            logger.info("El usuario ha entrado a la zona de administración correctamente");
            SceneLoader.loadScene(properties.getProperty("menu_view"), event);

        } else {
            logger.warn("El nombre de la cuenta o la contraseña introducidas por el usuario son incorrectas");
            showAlert(properties.getProperty("incorrect_current_data"));
        }
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void registerScene(Event event) {
        logger.info("El usuario se ha dirigido a la zona de registro");
        SceneLoader.loadScene(properties.getProperty("register_view"), event);
    }

    @FXML
    public void showPassword(ActionEvent event) {
        try {
            if (chkPassword.isSelected()) {
                setPasswordVisibility(true, properties.getProperty("image_closed"));
            } else {
                setPasswordVisibility(false, properties.getProperty("image_open"));
            }
        } catch (FileNotFoundException e) {
            logger.error("Las imagenes no se ha encontrado correctamente y por lo tanto han fallado", e);
        }
    }

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

    @FXML
    public void forgotPassword(MouseEvent event) {
        logger.info("El usuario se ha dirigido a la escena de recuperación de contraseña");
        SceneLoader.loadScene("pages/searchEmail-view.fxml", event);
    }

    @FXML
    public void onExit() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }
}