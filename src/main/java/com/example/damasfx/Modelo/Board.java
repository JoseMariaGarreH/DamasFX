package com.example.damasfx.Modelo;

import com.example.damasfx.Controladores.PlayController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board extends StackPane {
    private Piece piece;
    private static final int WIDTH = 125;
    private static final int HEIGHT = 120;
    private boolean isSelected;
    private PlayController playController;
    private ImageView invalidMove;

    public Board(boolean light, int x, int y, PlayController playController) {
        this.playController = playController;

        Rectangle border = new Rectangle(WIDTH, HEIGHT);
        border.setFill(light ? Color.BEIGE : Color.TAN);

        setOnMouseClicked(event -> {
            playController.clickBoard(this);
        });

        invalidMove = new ImageView(new Image(getClass().getResourceAsStream("/com/example/damasfx/img/cruz.png")));
        invalidMove.setFitWidth(100 * 0.5); // Ajustar el tamaño de la imagen
        invalidMove.setFitHeight(100 * 0.5);
        invalidMove.setVisible(false); // Invisible al principio

        getChildren().addAll(border,invalidMove);
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        if (piece != null) {
            getChildren().add(piece);
        }
    }

    public void removePiece() {
        getChildren().remove(piece);
        piece = null;
    }

    public void highlight() {
        isSelected = true;
        setOpacity(0.5);
    }

    public void clearHighlight() {
        isSelected = false;
        setOpacity(1.0);
    }

    public void showInvalidMoveIcon() {
        invalidMove.setVisible(true);
    }

    public void hideInvalidMoveIcon() {
        invalidMove.setVisible(false);
    }
}
