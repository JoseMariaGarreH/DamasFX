package com.example.damasfx.Modelo;

import com.example.damasfx.Enumerados.PieceType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Piece extends StackPane {
    private PieceType type;
    private boolean isKing;
    private ImageView kingImage;

    public Piece(PieceType type) {
        this.type = type;
        this.isKing = false;

        Circle circle = new Circle(100 * 0.4);
        circle.setFill(type == PieceType.RED ? Color.RED : Color.WHITE);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);

        // Intentar cargar la imagen del rey
        kingImage = new ImageView(new Image(getClass().getResourceAsStream("/com/example/damasfx/img/king.png")));
        kingImage.setFitWidth(100 * 0.5); // Ajustar el tamaño de la imagen
        kingImage.setFitHeight(100 * 0.5);
        kingImage.setVisible(false); // Invisible al principio

        getChildren().addAll(circle, kingImage);
    }

    public void setKing(boolean king) {
        isKing = king;
        kingImage.setVisible(king);
    }

    public boolean getIsKing() {
        return isKing;
    }

    public PieceType getType() {
        return type;
    }
}
