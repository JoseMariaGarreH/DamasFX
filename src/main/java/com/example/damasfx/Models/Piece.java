package com.example.damasfx.Models;

import com.example.damasfx.Enums.PieceType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Piece extends StackPane {

    private PieceType type; // Tipo de la pieza si es roja o blanca
    private boolean isKing; // Indica si la pieza es un rey
    private ImageView kingImage; // Imagen que representa la corona del rey


    public Piece(PieceType type) {
        this.type = type; // Asigna el tipo de la pieza
        this.isKing = false; // Al principio la pieza no es un rey

        // Crea un círculo para representar la pieza
        Circle circle = new Circle(100 * 0.4);
        // Establece el color del círculo basado en el tipo de la pieza
        circle.setFill(type == PieceType.RED ? Color.RED : Color.WHITE);
        circle.setStroke(Color.BLACK); // Color del borde del círculo
        circle.setStrokeWidth(2); // Ancho del borde del círculo

        // Crea la imagen para representar la corona del rey
        kingImage = new ImageView(new Image(getClass().getResourceAsStream("/com/example/damasfx/img/king.png")));
        // Ajusta el tamaño de la imagen
        kingImage.setFitWidth(100 * 0.5);
        kingImage.setFitHeight(100 * 0.5);
        // La imagen es invisible por defecto
        kingImage.setVisible(false);

        // Añade el círculo y la imagen del rey a la pieza
        getChildren().addAll(circle, kingImage);
    }

    // Método para establecer si la pieza es un rey
    public void setKing(boolean king) {
        isKing = king; // Asigna el valor de rey a la pieza
        kingImage.setVisible(king); // Muestra u oculta la imagen del rey
    }

    // Método para verificar si la pieza es un rey
    public boolean getIsKing() {
        return isKing; // Retorna el estado de rey de la pieza
    }

    // Método para obtener el tipo de la pieza
    public PieceType getType() {
        return type; // Retorna el tipo de la pieza
    }
}
