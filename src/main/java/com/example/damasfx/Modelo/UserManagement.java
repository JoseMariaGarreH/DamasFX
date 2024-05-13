package com.example.damasfx.Modelo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mongodb.client.result.UpdateResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static com.mongodb.client.model.Filters.*;

public class UserManagement {
    private static final String TABLE_NAME = "signed";
    private static MongoCollection<User> userCollection;
    private User currentUser;

    public UserManagement(MongoDatabase db) {
        userCollection = db.getCollection(TABLE_NAME, User.class);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public ObservableList getUserCollection() {
        ObservableList<User> userList = userCollection.find().into(FXCollections.observableArrayList());
        return userList;
    }

    public User userLogin (String login, String pass) {
        User user = userCollection.find(and(eq("password", pass),eq("login", login))).first();
        return user;
    }

    public Boolean insertNewUser (User newUser) {
        InsertOneResult x = userCollection.insertOne(newUser);
        return x.wasAcknowledged();
    }

    public boolean modifyUser(User newUser) {
        UpdateResult ur = userCollection.replaceOne(eq("_id",newUser.getId()),newUser);
        return ur.wasAcknowledged();
    }

    public boolean deleteUser(User newUser) {
        DeleteResult dr = userCollection.deleteOne(eq("login",newUser.getLogin()));
        return dr.wasAcknowledged();
    }

    public Boolean verifyUser(User newUser){
        String login = newUser.getLogin();

        if(newUser.getLogin() == null){
            return false;
        }

        User existingUser = userCollection.find((eq("login", login)), User.class).first();
        return existingUser == null;
    }

    public Boolean verifyEmail(User newUser){
        String EMAIL_PATTERN = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9-]{2,}[.][a-zA-Z]{2,4}$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);

        if (newUser.getEmail() == null) {
            return false;
        }

        Matcher matcher = pattern.matcher(newUser.getEmail());
        if (!matcher.matches()) {
            return false;
        }

        String email = newUser.getEmail();
        User existingUser = userCollection.find(eq("email", email), User.class).first();
        return existingUser == null;
    }
}
