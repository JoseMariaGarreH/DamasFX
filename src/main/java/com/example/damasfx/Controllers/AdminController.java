package com.example.damasfx.Controllers;

import com.example.damasfx.Enums.ActionType;
import com.example.damasfx.Main;
import com.example.damasfx.Utils.SceneLoader;
import com.example.damasfx.Utils.UserManagement;
import com.example.damasfx.Models.*;
import com.example.damasfx.Services.DataBase;
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

    private Properties properties = new Properties(); // Propiedades cargadas desde un archivo de configuración
    private UserManagement userCollection = DataBase.getInstance().getUserCollection(); // Gestión de usuarios

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();

        // Obtener usuario actual y establecer nombre de usuario en la etiqueta
        UserManagement sesionManagement = new UserManagement();
        userCollection.setCurrentUser(userCollection.getUserById(sesionManagement.getLoggedInUser()));
        outputName.setText(userCollection.getCurrentUser().getLogin());

        // Configurar las columnas de la tabla con los datos de usuario
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleType"));
        nacionalityColumn.setCellValueFactory(new PropertyValueFactory<>("nacionality"));

        // Establecer los datos de la tabla con la colección de usuarios
        userTable.setItems(userCollection.getUserCollection());
    }

    // Método para cargar propiedades desde un archivo
    private void loadProperties() {
        try {
            InputStream input = SecondUserController.class.getClassLoader().getResourceAsStream("general.properties");
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

    @FXML
    private void handlerAdd(ActionEvent event) {
        try {
            // Cargar la vista para agregar usuario
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(properties.getProperty("window_view")));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            Image icon = new Image(getClass().getResourceAsStream(properties.getProperty("application_icon")));
            stage.getIcons().add(icon);
            stage.setResizable(false);
            // Define que esta ventana es modal, bloqueando la interacción con otras ventanas hasta que se cierre
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            // Obtener el controlador de la vista cargada
            WindowController windowController = loader.getController();
            // Establecer el tipo de acción en el controlador
            windowController.setActionType(ActionType.GUARDAR);

            // Obtener usuario devuelto y agregarlo a la colección
            Users u = windowController.getCurrentUser();
            if (u != null) {
                boolean isInserted = userCollection.insertNewUser(u);
                if (isInserted) {
                    // Actualizar la lista observable con el nuevo usuario
                    ObservableList<Users> data = userTable.getItems();
                    logger.info("Se ha añadido el usuario " + u.getName() + " " + u.getSurname());
                    data.add(u); //Añade el nuevo usuario
                }
            }
        } catch (IOException ex) {
            logger.error("Error al cargar la pantalla de inserción", ex);
            showAlert(Alert.AlertType.INFORMATION, "Error", ex.getMessage());
        }
    }


    @FXML
    public void handlerModify(ActionEvent event) {
        // Obtiene el usuario seleccionado en la tabla
        Users u = userTable.getSelectionModel().getSelectedItem();

        // Verifica si un usuario ha sido seleccionado
        if (u != null) {
            try {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource(properties.getProperty("window_view")));
                Parent root = loader.load(); // Carga la estructura de la interfaz desde el archivo FXML

                // Obtiene el controlador de la ventana cargada y configura el tipo de acción a MODIFICAR
                WindowController windowController = loader.getController();
                windowController.setActionType(ActionType.MODIFICAR); // Define la acción que se va a realizar
                windowController.initAttributes(u);

                // Crear una nueva escena con el contenido cargado
                Scene scene = new Scene(root);
                Stage stage = new Stage();

                Image icon = new Image(getClass().getResourceAsStream(properties.getProperty("application_icon")));
                stage.getIcons().add(icon);
                stage.setResizable(false);
                // Define que esta ventana es modal, bloqueando la interacción con otras ventanas hasta que se cierre
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();

                // Obtener el usuario modificado desde el controlador de la ventana
                Users aux = windowController.getCurrentUser();
                if (aux != null) {
                    // Intentar modificar el usuario en la colección
                    boolean isModified = userCollection.modifyUser(aux);
                    if (isModified) {
                        // Si la modificación fue exitosa, actualizar la tabla de usuarios
                        ObservableList<Users> data = userTable.getItems();
                        int index = data.indexOf(u); // Encuentra el índice del usuario original
                        if (index != -1) {
                            data.set(index, aux); // Reemplaza el usuario original con el usuario modificado
                        }
                        // Registro de la modificación exitosa
                        logger.info("Se ha modificado el usuario " + u.getName() + " " + u.getSurname());
                        userTable.getSelectionModel().clearSelection(); // Limpiar la selección de la tabla
                    } else {
                        // Si la modificación falla debido a un usuario duplicado, mostrar una alerta
                        showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("alert_duplicate_user"));
                    }
                }
            } catch (IOException ex) {
                // Manejo de excepciones en caso de error al cargar la vista
                logger.error("Error al cargar la pantalla de edición", ex);
            }
        } else {
            // Si no se ha seleccionado ningún usuario, mostrar una advertencia y alerta
            logger.warn("El usuario no ha seleccionado un usuario");
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("unselected_user"));
        }
    }

    @FXML
    public void handlerDelete(ActionEvent event) {
        // Obtiene el usuario seleccionado en la tabla
        Users u = userTable.getSelectionModel().getSelectedItem();

        // Verifica si un usuario ha sido seleccionado
        if (u != null) {
            // Intenta eliminar el usuario seleccionado de la colección de usuarios
            boolean isDeleted = userCollection.deleteUser(u);

            // Si la eliminación fue exitosa
            if (isDeleted) {
                // Obtiene la lista observable de usuarios de la tabla
                ObservableList<Users> data = userTable.getItems();
                // Registra la eliminación exitosa del usuario en los logs
                logger.info("Se ha eliminado el usuario " + u.getName() + " " + u.getSurname());
                data.remove(u); // Elimina el usuario de la lista observable

                // Limpia la selección de la tabla
                userTable.getSelectionModel().clearSelection();
                showAlert(Alert.AlertType.INFORMATION, "Info", properties.getProperty("deleted_user_correctly"));
            }
        } else {
            // Si no se ha seleccionado ningún usuario, registra una advertencia y muestra una alerta de error
            logger.warn("El usuario no ha seleccionado un usuario");
            showAlert(Alert.AlertType.ERROR, "Error", properties.getProperty("unselected_user"));
        }
    }


    // Método para mostrar alertas
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Evento del botón para navegar al menú principal
    @FXML
    public void goToMenu(ActionEvent event) {
        logger.info("El usuario ha entrado al menu principal");
        SceneLoader.loadScene(properties.getProperty("menu_view"), event);
    }

    // Evento del botón para salir y volver a la pantalla de inicio de sesión
    @FXML
    public void exit(ActionEvent event) {
        logger.info("El usuario ha vuelto a la pantalla de inicio de sesión");
        userCollection.clearLoggedInUser();
        SceneLoader.loadScene(properties.getProperty("start_view"), event);
    }
}
