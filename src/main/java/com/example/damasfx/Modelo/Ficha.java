package com.example.damasfx.Modelo;
import java.awt.Color;

public  class Ficha{
    private int xPos;
    private int yPos;
    private boolean isKing;
    private final Color color;


    public Ficha(int xPos, int yPos,Color color) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.color = color;
        isKing = false;
    }

    public Color getColor() {
        return color;
    }
    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public boolean isKing() {
        return isKing;
    }

    public void setKing(boolean king) {
        this.isKing = king;
    }

    public void makeKing() {
        isKing = true;
    }

    @Override
    public String toString() {
        return "Ficha{" + "xPos=" + xPos + ", yPos=" + yPos + ", esReina=" + isKing + ", color=" + color + '}';
    }
}
