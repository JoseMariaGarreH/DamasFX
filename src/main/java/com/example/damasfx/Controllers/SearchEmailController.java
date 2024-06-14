package com.example.damasfx.Controllers;


import com.example.damasfx.Models.Users;
import com.example.damasfx.Services.DataBase;
import com.example.damasfx.Utils.SceneLoader;
import com.example.damasfx.Utils.UserManagement;
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
    private UserManagement userCollection = DataBase.getInstance().getUserCollection(); // Gestión de usuarios
    private Properties properties = loadProperties(); // Cargamos el archivo properties

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();
    }

    // Método para cargar las propiedades desde un archivo de configuración.
    private Properties loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("general.properties");
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error loading email configuration: " + ex);
        }
        return properties;
    }

    @FXML
    public void checkEmail(ActionEvent event) {
        // Recogemos el email registrado
        String txtEmail = inputEmail.getText().trim();
        // Buscamos el email en la base de datos
        Users user = userCollection.searchEmail(txtEmail);
        // Si existe un usuario con ese email entra
        if (user != null) {
            // Direccion del correo que envia el correo electrónico
            String fromEmail = properties.getProperty("email");
            // Contraseña de la aplicación
            String passwordEmail = properties.getProperty("password_email");

            properties.put("mail.smtp.auth", "true"); //identificación requerida
            properties.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP
            properties.put("mail.smtp.host", "smtp.gmail.com"); //servidor SMTP
            properties.put("mail.smtp.port", "587"); //El puerto SMTP seguro de Google

            //abre una nueva sesión contra el servidor basada en:
            //el usuario, la contraseña y las propiedades especificadas
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, passwordEmail);
                }
            });

            try {
                //compone el mensaje
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
                //asunto
                message.setSubject("Contraseña");
                //cuerpo del mensaje
                message.setText("Su contraseña es: " + user.getPassword());
                //envía el mensaje, realizando conexión, transmisión y desconexión
                Transport.send(message);
                showAlert(Alert.AlertType.INFORMATION,"Información","El email se ha enviado correctamente.");

            } catch (MessagingException e) {
                logger.error("Error al enviar el email: " + e);
                showAlert(Alert.AlertType.ERROR,"Error","Error al enviar el email. Por favor, inténtelo de nuevo.");
            }

        } else { // Si no muestra mensaje de error como que no ha encontrado el email
            showAlert(Alert.AlertType.ERROR,"Error","El email que ha introducido no se ha podido encontrar.");
        }
    }

    // Método que vuelve a la escena de inicio de sesión
    @FXML
    public void exitToStart(ActionEvent event){
        logger.info("El usuario ha vuelto a la pantalla de inicio de sesión desde la pantalla 'searchEmail-view'");
        SceneLoader.loadScene(properties.getProperty("start_view"), event);
    }

    // Método para mostrar una alerta con el mensaje de error
    private void showAlert(Alert.AlertType alertType,String title,String content) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
