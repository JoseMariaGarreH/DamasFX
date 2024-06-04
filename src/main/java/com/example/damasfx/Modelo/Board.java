package com.example.damasfx.Modelo;

import com.example.damasfx.Controladores.PlayController;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board extends StackPane {
    private Piece piece;
    private static final int WIDTH = 135;
    private static final int HEIGHT = 115;
    private boolean isSelected;
    private PlayController playController;

    public Board(boolean light, int x, int y, PlayController playController) {
        this.playController = playController;

        Rectangle border = new Rectangle(WIDTH, HEIGHT);
        border.setFill(light ? Color.BEIGE : Color.TAN);

        setOnMouseClicked(event -> {
            playController.handleTileClick(this);
        });

        getChildren().add(border);
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
        setOpacity(0.7);
    }

    public void clearHighlight() {
        isSelected = false;
        setOpacity(1.0);
    }
}
