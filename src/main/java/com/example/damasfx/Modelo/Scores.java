package com.example.damasfx.Modelo;

import org.bson.types.ObjectId;

public class Scores {
    private ObjectId id;
    private String userId;
    private int score;
    private String userName;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Scores(){
    }
    public Scores(ObjectId id, int score) {
        this.id = id;
        this.score = score;
    }
}

