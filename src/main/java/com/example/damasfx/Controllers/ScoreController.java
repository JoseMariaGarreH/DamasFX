package com.example.damasfx.Controllers;

import com.example.damasfx.Utils.SceneLoader;
import com.example.damasfx.Utils.UserManagement;
import com.example.damasfx.Models.Users;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;


public class ScoreController implements Initializable {
    // Logger para registrar mensajes de log
    private static final Logger logger = LogManager.getLogger(ScoreController.class);

    @FXML
    private Label name;  // Etiqueta para mostrar el nombre del usuario
    @FXML
    private Label score;  // Etiqueta para mostrar el puntaje del usuario
    @FXML
    private TableView<Users> scoreTable;  // Tabla para mostrar los puntajes de los usuarios

    @FXML
    private TableColumn<Users, String> playerColumn;  // Columna de la tabla para el nombre de usuario
    @FXML
    private TableColumn<Users, Integer> scoreColumn;  // Columna de la tabla para el puntaje
    @FXML
    private TableColumn<Users, String> rankingColumn;  // Columna de la tabla para el ranking

    // Gestión de usuarios
    private UserManagement userCollection;
    // Propiedades para cargar configuraciones
    private Properties properties = new Properties();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();

        // Gestionar la sesión del usuario
        UserManagement sessionManagement = new UserManagement();
        userCollection = new UserManagement();  // Inicializar correctamente userCollection
        userCollection.setCurrentUser(userCollection.getUserById(sessionManagement.getLoggedInUser()));

        // Obtener el usuario actual y actualizar las etiquetas de nombre y puntaje
        Users currentUser = userCollection.getCurrentUser();
        name.setText(currentUser.getLogin());
        score.setText(String.valueOf(currentUser.getScore()));

        // Configurar las columnas de la tabla con los valores correspondientes
        playerColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        // Permitir que la columna de puntaje sea ordenable
        scoreColumn.setSortable(true);

        // Configurar la columna de ranking para mostrar el ranking basado en el orden de la tabla
        rankingColumn.setCellValueFactory(cellData -> {
            int rank = scoreTable.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleStringProperty(String.valueOf(rank));
        });

        // Establecer los datos en la tabla
        scoreTable.setItems(FXCollections.observableArrayList(userCollection.getUserCollection()));

        // Ordenar la tabla por puntaje en orden descendente
        scoreColumn.setSortType(TableColumn.SortType.DESCENDING);
        scoreTable.getSortOrder().add(scoreColumn);
        scoreTable.sort();
    }

    // Método para cargar las propiedades desde un archivo de configuración.
    private void loadProperties() {
        try (InputStream input = StartController.class.getClassLoader().getResourceAsStream("general.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }

     // Método para manejar el evento de salida. Cambia la vista a la del menú principal.
    @FXML
    public void exit(ActionEvent event){
        // Registrar un mensaje de información cuando el usuario entra al menú principal
        logger.info("El usuario ha entrado al menu principal");
        // Cargar la escena del menú principal usando SceneLoader
        SceneLoader.loadScene(properties.getProperty("menu_view"), event);
    }
}
