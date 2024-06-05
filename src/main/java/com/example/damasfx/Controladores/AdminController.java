package com.example.damasfx.Controladores;

import com.example.damasfx.Enumerados.ActionType;
import com.example.damasfx.Main;
import com.example.damasfx.Gestion.SceneLoader;
import com.example.damasfx.Gestion.UserManagement;
import com.example.damasfx.Modelo.*;
import com.example.damasfx.VDataBase.DataBase;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Properties;
import java.util.ResourceBundle;

import static com.mongodb.client.model.Filters.eq;

public class AdminController implements Initializable {
    private static final Logger logger = LogManager.getLogger(AdminController.class);
    @FXML
    private Label outputName;
    @FXML
    private TableColumn<Users, String> loginColumn;
    @FXML
    private TableColumn<Users, String> passwordColumn;
    @FXML
    private TableColumn<Users, String> emailColumn;
    @FXML
    private TableColumn<Users, String> nacionalityColumn;
    @FXML
    private TableColumn<Users, String> nameColumn;
    @FXML
    private TableColumn<Users, String> surnameColumn;
    @FXML
    private TableColumn<Users, String> roleColumn;
    @FXML
    private TableColumn<Users, LocalDate> dateColumn;
    @FXML
    private TableView<Users> userTable;
    private Properties properties = new Properties();
    private UserManagement userCollection = DataBase.getInstance().getUserCollection();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();
        UserManagement sesionManagement = new UserManagement();
        userCollection.setCurrentUser(userCollection.getUserById(sesionManagement.getLoggedInUser()));
        outputName.setText(userCollection.getCurrentUser().getName());

        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleType"));
        nacionalityColumn.setCellValueFactory(new PropertyValueFactory<>("nacionality"));

        userTable.setItems(userCollection.getUserCollection());
    }

    private void loadProperties() {
        try (InputStream input = AdminController.class.getClassLoader().getResourceAsStream("general.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

    @FXML
    private void handlerAdd(ActionEvent event) {
        try {
            // Cargo la vista
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("pages/window-view.fxml"));

            // Creo el Scene
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            Image icon = new Image(getClass().getResourceAsStream(properties.getProperty("application_icon")));
            stage.getIcons().add(icon);

            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            WindowController windowController = loader.getController();
            windowController.setActionType(ActionType.GUARDAR);

            // cojo la persona devuelta
            Users u = windowController.getCurrentUser();
            if (u != null) {
                // Añado la persona
                boolean isInserted = userCollection.insertNewUser(u);
                if (isInserted) {
                    ObservableList<Users> data = userTable.getItems();
                    logger.info("Se ha añadido el usuario " + u.getName() + " " + u.getSurname());
                    data.add(u);
                }
            }

        } catch (IOException ex) {
            logger.error("Error al cargar la pantalla de inserción");
            showAlert(Alert.AlertType.INFORMATION, "Error", ex.getMessage());
        }
    }

    @FXML
    public void handlerModify(ActionEvent event) {
        Users u = userTable.getSelectionModel().getSelectedItem();

        if (u != null) {
            try {
                // Cargo la vista
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("pages/window-view.fxml"));

                // Cargo la ventana
                Parent root = loader.load();

                // Cojo el controlador
                WindowController windowController = loader.getController();
                windowController.setActionType(ActionType.MODIFICAR);
                windowController.initAttributes(u);

                // Creo el Scene
                Scene scene = new Scene(root);
                Stage stage = new Stage();

                Image icon = new Image(getClass().getResourceAsStream(properties.getProperty("application_icon")));
                stage.getIcons().add(icon);

                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();

                // Cojo la persona devuelta
                Users aux = windowController.getCurrentUser();
                if (aux != null) {
                    boolean isModified = userCollection.modifyUser(aux);
                    if (isModified) {
                        ObservableList<Users> data = userTable.getItems();
                        int index = data.indexOf(u);
                        if (index != -1) {
                            data.set(index, aux);
                        }
                        logger.info("Se ha modificado el usuario " + u.getName() + " " + u.getSurname());
                        userTable.getSelectionModel().clearSelection();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_duplicate_user"));
                    }
                }
            } catch (IOException ex) {
                logger.error("Error al cargar la pantalla de edición");
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        } else {
            logger.warn("El usuario no ha seleccionado un usuario");
            showAlert(Alert.AlertType.ERROR, "Error", "Debes seleccionar una persona");
        }
    }

    @FXML
    public void handlerDelete(ActionEvent event) {
        Users u = userTable.getSelectionModel().getSelectedItem();

        if (u != null) {
            // Elimino la persona
            boolean isDeleted = userCollection.deleteUser(u);
            if (isDeleted) {
                ObservableList<Users> data = userTable.getItems();
                logger.info("Se ha eliminado el usuario " + u.getName() + " " + u.getSurname());
                data.remove(u);
                userTable.getSelectionModel().clearSelection();
                showAlert(Alert.AlertType.INFORMATION, "Info", properties.getProperty("deleted_user_correctly"));
            }
        } else {
            logger.warn("El usuario no ha seleccionado un usuario");
            showAlert(Alert.AlertType.ERROR, "Error", "Debes seleccionar una persona");
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
    public void goToMenu(ActionEvent event) {
        logger.info("El usuario ha entrado al menu principal");
        SceneLoader.loadScene("pages/menu-view.fxml", event);
    }

    @FXML
    public void exit(ActionEvent event) {
        logger.info("El usuario ha vuelto a la pantalla de inicio de sesión");
        userCollection.clearLoggedInUser();
        SceneLoader.loadScene("pages/start-view.fxml", event);
    }
}
