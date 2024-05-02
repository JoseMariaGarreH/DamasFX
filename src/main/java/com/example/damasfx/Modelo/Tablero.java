package com.example.damasfx.Modelo;

import javafx.scene.layout.GridPane;

import javafx.scene.control.Button;

public class Tablero {

    public static final int NUM_CELDAS = 8; // número de celdas en una fila o columna

    public GridPane crearTablero() {
        GridPane grid = new GridPane();

        for (int y = 0; y < NUM_CELDAS; y++) {
            for (int x = 0; x < NUM_CELDAS; x++) {
                Button boton = new Button();
                String color = (x + y) % 2 == 0 ? "-fx-background-color: white;" : "-fx-background-color: black;";
                boton.setStyle(color);
                boton.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
                grid.add(boton, x, y);
            }
        }

        return grid;
    }
}


