package com.example.damasfx.Modelo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static com.mongodb.client.model.Filters.*;

public class UserManagement {
    private static final String TABLE_NAME = "signed";
    private static MongoCollection<User> userCollection;

    public UserManagement(MongoDatabase db) {
        userCollection = db.getCollection(TABLE_NAME, User.class);
    }

    public ObservableList getUserCollection() {
        ObservableList<User> userList = userCollection.find().into(FXCollections.observableArrayList());
        return userList;
    }

    public User userLogin (String login, String pass) {
        User user = userCollection.find(and(eq("password", pass),eq("login", login))).first();
        return user;
    }

    public void insertNewUser (User newUser) {
        InsertOneResult x = userCollection.insertOne(newUser);
        x.wasAcknowledged();
    }


    public Boolean verifyUser(User newUser){
        String login = newUser.getLogin();
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
