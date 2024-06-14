package com.example.damasfx.Models;

import com.example.damasfx.Enums.ResidenceCountry;
import com.example.damasfx.Enums.RoleType;
import org.bson.types.ObjectId;

import java.util.Date;

public class Users implements Cloneable{
    private ObjectId id;
    private String login;
    private String name;
    private String surname;
    private RoleType roleType;
    private Date dateOfBirth;
    private String password;
    private String email;
    private ResidenceCountry nacionality;
    private int score;

    public Users(){
        this.id = new ObjectId();
    }
    public Users(String login, String name, String surname, RoleType roleType, Date dateOfbirth, String password, String email, ResidenceCountry nacionality,int score) {
        this.id = new ObjectId();
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.roleType = roleType;
        this.dateOfBirth = dateOfbirth;
        this.password = password;
        this.email = email;
        this.nacionality = nacionality;
        this.score = score;
    }

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateofBirth) {
        this.dateOfBirth = dateofBirth;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", role=" + roleType +
                ", dateOfbirth=" + dateOfBirth +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", nacionality=" + nacionality +
                '}';
    }
}
