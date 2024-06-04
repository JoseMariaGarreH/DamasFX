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

    public String  getUserId() {
        return userId;
    }

    public void setUserId(String  userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Scores(){
    }
    public Scores(ObjectId id,String userId, int score, String userName) {
        this.id = id;
        this.userId = userId;
        this.score = score;
        this.userName = userName;
    }
}

