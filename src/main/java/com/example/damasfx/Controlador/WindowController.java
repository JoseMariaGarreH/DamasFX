package com.example.damasfx.Controlador;

import com.example.damasfx.Modelo.ResidenceCountry;
import com.example.damasfx.Modelo.RoleType;
import com.example.damasfx.Modelo.User;
import com.example.damasfx.Modelo.UserManagement;
import com.example.damasfx.Vista.DataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class WindowController implements Initializable {
    @FXML
    private Button btnSave;

    @FXML
    private Button btnExit;

    @FXML
    private ComboBox<ResidenceCountry> boxNacionality;

    @FXML
    private TextField inputSurname;
    @FXML
    private DatePicker inputDate;
    @FXML
    private TextField inputLogin;
    @FXML
    private TextField inputPassword;

    @FXML
    private TextField inputName;

    @FXML
    private TextField inputEmail;

    @FXML
    private ComboBox<RoleType> boxRole;
    ObservableList<ResidenceCountry> listOfCountries = FXCollections.observableArrayList(ResidenceCountry.values());
    ObservableList<RoleType> listOfRoles = FXCollections.observableArrayList(RoleType.values());
    private final UserManagement userCollection = DataBase.getInstance().getUserCollection();
    private User user;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.boxNacionality.setItems(listOfCountries);
        this.boxRole.setItems(listOfRoles);
    }

    public void initAttributtes(User u) {
        this.user = u;
        // cargo los datos de la persona
        this.inputLogin.setText(u.getLogin());
        this.inputPassword.setText(u.getPassword());
        this.boxRole.setValue(u.getRoleType());
        this.inputName.setText(u.getName());
        this.inputSurname.setText(u.getSurname());
        LocalDate localDate = (u.getDateOfBirth() != null)? u.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : LocalDate.now();
        this.inputDate.setValue(localDate);
        this.inputEmail.setText(u.getEmail());
        this.boxNacionality.setValue(u.getNacionality());
    }

    @FXML
    private void save(ActionEvent event) {
        // Cojo los datos
        String login = this.inputLogin.getText();
        String password = this.inputPassword.getText();
        RoleType role = this.boxRole.getValue();
        String name = this.inputName.getText();
        String surname = this.inputSurname.getText();
        LocalDate localDate = this.inputDate.getValue();
        Date date;
        if (localDate != null) {
            Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
            date = Date.from(instant);
        } else {
            date = new Date();
        }
        String email = this.inputEmail.getText();
        ResidenceCountry country = this.boxNacionality.getValue();

        boolean emptyFields = hasEmptyFields(login,password,email,role);

        if(!emptyFields){
            showAlert(Alert.AlertType.ERROR, "Error", "Rellena los campos obligatorios");
            return;
        }

        // Creo la persona
        User u = new User(login,password,name,role,date,email,surname,country);

        if(userCollection.verifyEmail(u)) {
            // Modificar
            if (this.user != null) {
                // Modifico el objeto
                this.user.setLogin(login);
                this.user.setPassword(password);
                this.user.setRoleType(role);
                this.user.setName(name);
                this.user.setSurname(surname);
                this.user.setEmail(email);
                this.user.setNacionality(country);

                showAlert(Alert.AlertType.INFORMATION, "Información", "Se ha modificado correctamente");
            } else {
                // insertando
                // Compruebo si la persona existe
                if(userCollection.verifyUser(u)) {
                    this.user = u;
                }else{
                    showAlert(Alert.AlertType.ERROR, "Error", "El login que ha introducido ya existe");
                }
            }
            // Cerrar la ventana
            Stage stage = (Stage) this.btnSave.getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "El email que ha introducido ya existe o esta mal introducido");
        }
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
        this.user = null;
        // Cerrar la ventana
        Stage stage = (Stage) this.btnSave.getScene().getWindow();
        stage.close();
    }
    private boolean hasEmptyFields(String accountText, String emailText, String passwordText, RoleType roleType) {
        return !accountText.trim().isEmpty() &&
                !emailText.trim().isEmpty() &&
                !(roleType == null) &&
                !passwordText.trim().isEmpty();
    }

    public User getUser() {
        return user;
    }
}
