package com.example.damasfx.Modelo;

import org.bson.types.ObjectId;

public class User {
    private ObjectId id;
    private String login;
    private String name;
    private String surname;
    private String password;
    private String email;

    public User(){
    }
    public User(String login, String name,String surname ,String password, String email, ResidenceCountry nacionality) {
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
        this.nacionality = nacionality;
    }

    private ResidenceCountry nacionality;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApellidos() {
        return surname;
    }

    public void setApellidos(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public ResidenceCountry getNacionality() {
        return nacionality;
    }

    public void setNacionality(ResidenceCountry nacionality) {
        this.nacionality = nacionality;
    }



    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", nacionality=" + nacionality +
                '}';
    }
}
