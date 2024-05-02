package com.example.damasfx.Controlador;

import com.example.damasfx.Vista.DataBase;
import com.example.damasfx.Main;
import com.example.damasfx.Modelo.User;
import com.example.damasfx.Modelo.UserManagement;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class StartController implements Initializable {
    @FXML
    private ToggleButton chkPassword;
    @FXML
    private TextField txtAccount;
    @FXML
    private TextField txtPassordVisible;
    @FXML
    private ImageView image;
    @FXML
    private PasswordField txtPasswordNotVisible;
    private UserManagement userCollection = DataBase.getInstance().getUserCollection();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtAccount.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                logIn(event);
                event.consume();
            }
        });
        txtPassordVisible.setOnKeyPressed(txtAccount.getOnKeyPressed());
        txtPasswordNotVisible.setOnKeyPressed(txtAccount.getOnKeyPressed());
    }

    @FXML
    public void logIn(Event event) {
        User user;
        if (!txtPassordVisible.getText().isEmpty()) {
            user = userCollection.userLogin(txtAccount.getText(), txtPassordVisible.getText());
        } else {
            user = userCollection.userLogin(txtAccount.getText(), txtPasswordNotVisible.getText());
        }

        if (user != null) {
            System.out.println(user);
            loadScene("menu-view.fxml",event);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("El nombre de la cuenta o la contraseña son incorrectas");
            alert.showAndWait();
        }
    }

    @FXML
    public void registerScene(Event event) {
        loadScene("register-view.fxml",event);
    }

    public void loadScene(String fxmlPath, Event event) {
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

    @FXML
    public void showPassword(ActionEvent event) throws FileNotFoundException {
        if(chkPassword.isSelected()) {
            File img = new File("src/main/resources/com/example/damasfx/img/ojo_cerrado.png");
            InputStream cerrado = new FileInputStream(img);
            image.setImage(new Image(cerrado));
            txtPassordVisible.setText(txtPasswordNotVisible.getText());
            txtPassordVisible.setVisible(true);
            txtPasswordNotVisible.setVisible(false);
        }else {
            File img = new File("src/main/resources/com/example/damasfx/img/ojo_abierto.png");
            InputStream abierto = new FileInputStream(img);
            image.setImage(new Image(abierto));
            txtPasswordNotVisible.setText(txtPassordVisible.getText());
            txtPassordVisible.setVisible(false);
            txtPasswordNotVisible.setVisible(true);
        }
    }

    @FXML
    public void cancelled(){
        txtAccount.setText("");
        txtPassordVisible.setText("");
        txtPasswordNotVisible.setText("");
        chkPassword.setSelected(false);
    }

}
