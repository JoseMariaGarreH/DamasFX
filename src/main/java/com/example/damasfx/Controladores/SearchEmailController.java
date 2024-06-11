package com.example.damasfx.Controladores;

import com.example.damasfx.Gestion.SceneLoader;
import com.example.damasfx.Gestion.UserManagement;
import com.example.damasfx.Modelo.Users;
import com.example.damasfx.VDataBase.DataBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.mail.*;
import javax.mail.internet.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class SearchEmailController implements Initializable {
    private static final Logger logger = LogManager.getLogger(SearchEmailController.class);

    @FXML
    private TextField inputEmail;
    private static String email;
    private static String passwordEmail;

    private UserManagement userCollection = DataBase.getInstance().getUserCollection();
    private Properties properties = new Properties();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();
    }

    private void loadProperties() {
        try{
            InputStream input = SecondUserController.class.getClassLoader().getResourceAsStream("general.properties");
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

    @FXML
    public void checkEmail(ActionEvent event) throws IOException {
        String txtEmail = inputEmail.getText().trim();
        Users user = userCollection.searchEmail(txtEmail);

        if (user != null) {
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");

            email = properties.getProperty("email");
            passwordEmail = properties.getProperty("password_email");

            Session session = Session.getInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(email, passwordEmail);
                        }
                    });
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(email));
                message.setSubject("Contraseña");
                message.addRecipient(Message.RecipientType.TO,new InternetAddress(user.getEmail()));
                message.setText(user.getPassword());
                Transport.send(message);
            } catch (Exception e) {
                logger.error("Error al enviar el email "+ e);
            }
        } else {
            showAlert("El email que ha introducido no se ha podido encontrar");
        }
    }

    @FXML
    public void exitToStart(ActionEvent event){
        logger.info("El usuario ha vuelto a la pantalla de inicio de sesión desde la pantalla 'searchEmail-view'");
        SceneLoader.loadScene("pages/start-view.fxml", event);
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.setContentText(content);
        alert.showAndWait();
    }
}
