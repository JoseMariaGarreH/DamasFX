package com.example.damasfx.Models;

import com.example.damasfx.Controllers.PlayController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board extends StackPane {
    private Piece piece; // Pieza contenida en la casilla, si la hay
    private static final int WIDTH = 125; // Ancho de la casilla
    private static final int HEIGHT = 120; // Alto de la casilla
    private PlayController playController; // Controlador para gestionar eventos de clic
    private ImageView invalidMove; // Icono que indica un movimiento inválido

    public Board(boolean light, PlayController playController) {
        this.playController = playController; // Asocia el controlador de juego

        // Crea un rectángulo para representar el borde de la casilla
        Rectangle border = new Rectangle(WIDTH, HEIGHT);
        // Establece el color del borde basado en si es una casilla clara u oscura
        border.setFill(light ? Color.BEIGE : Color.TAN);

        // Configura el evento de clic en la casilla
        setOnMouseClicked(event -> {
            this.playController.clickBoard(this); // Llama al controlador de juego para manejar el clic
        });

        // Crea la imagen para indicar un movimiento inválido
        invalidMove = new ImageView(new Image(getClass().getResourceAsStream("/com/example/damasfx/img/cruz.png")));
        // Ajusta el tamaño de la imagen
        invalidMove.setFitWidth(100 * 0.5);
        invalidMove.setFitHeight(100 * 0.5);
        // La imagen es invisible por defecto
        invalidMove.setVisible(false);

        // Añade el borde y el icono de movimiento inválido a la casilla
        getChildren().addAll(border, invalidMove);
    }

    // Método para verificar si la casilla tiene una pieza
    public boolean hasPiece() {
        return piece != null;
    }

    // Método para obtener la pieza en la casilla
    public Piece getPiece() {
        return piece;
    }

    // Método para establecer una pieza en la casilla
    public void setPiece(Piece piece) {
        this.piece = piece;
        if (piece != null) {
            getChildren().add(piece); // Añade la pieza a los hijos de la casilla
        }
    }

    // Método para remover la pieza de la casilla
    public void removePiece() {
        getChildren().remove(piece); // Remueve la pieza de la casilla
        piece = null; // Establece la referencia de la pieza a null
    }

    // Método para resaltar la casilla
    public void highlight() {
        setOpacity(0.5); // Cambia la opacidad para resaltar
    }

    // Método para quitar el resaltado de la casilla
    public void clearHighlight() {
        setOpacity(1.0); // Restaura la opacidad original
    }

    // Método para mostrar el icono de movimiento inválido
    public void showInvalidMoveIcon() {
        invalidMove.setVisible(true); // Hace visible el icono
    }

    // Método para ocultar el icono de movimiento inválido
    public void hideInvalidMoveIcon() {
        invalidMove.setVisible(false); // Hace invisible el icono
    }
}
