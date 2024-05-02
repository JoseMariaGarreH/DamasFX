package com.example.damasfx.Controlador;

import com.example.damasfx.Modelo.Tablero;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PlayController implements Initializable {
    @FXML
    private GridPane contenedorTablero; // Este es el GridPane en tu archivo FXML donde quieres mostrar el tablero de damas

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tablero tablero = new Tablero();
        moveNodes(tablero.crearTablero(),contenedorTablero);
    }

    private void moveNodes(GridPane from, GridPane to) {
        // Recopilar nodos del primer GridPane en una lista
        List<Node> nodes = new ArrayList<>(from.getChildren());

        // Limpiar el primer GridPane
        from.getChildren().clear();

        // Agregar los nodos al nuevo GridPane
        for (Node node : nodes) {
            // Obtener la posición original del nodo
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            // Agregar el nodo al nuevo GridPane en la misma posición
            to.add(node, colIndex == null ? 0 : colIndex, rowIndex == null ? 0 : rowIndex);
        }
    }


}

