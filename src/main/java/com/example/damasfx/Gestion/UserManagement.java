package com.example.damasfx.Gestion;

import com.example.damasfx.Controladores.StartController;
import com.example.damasfx.Modelo.Users;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.prefs.Preferences;


import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;


public class UserManagement {
    private static final Logger logger = LogManager.getLogger(StartController.class);
    private static final String TABLE_NAME = "USERS_SIGNED";
    private static MongoCollection<Users> userCollection;
    private Users currentUsers;
    private static final String USERNAME_KEY = "loggedInUser";
    private final Preferences prefs;

    public UserManagement(){
        prefs = Preferences.userNodeForPackage(UserManagement.class);
    }

    public UserManagement(MongoDatabase db) {
        userCollection = db.getCollection(TABLE_NAME, Users.class);
        prefs = Preferences.userNodeForPackage(UserManagement.class);
    }

    public void saveLoggedInUser(String username) {
        prefs.put(USERNAME_KEY, username);
    }

    public String getLoggedInUser() {
        return prefs.get(USERNAME_KEY, null);
    }

    public void clearLoggedInUser() {
        prefs.remove(USERNAME_KEY);
    }

    public Users getCurrentUser() {
        return currentUsers;
    }

    public void setCurrentUser(Users currentUsers) {
        this.currentUsers = currentUsers;
    }

    public ObservableList<Users> getUserCollection() {
        return userCollection.find().into(FXCollections.observableArrayList());
    }

    public Users getUserById(String userId) {
        Users user;
        user = userCollection.find(eq("login", userId)).first();
        return user;
    }

    public Users userLogin(String login, String pass) {
        String encryptedPass = encryptPasswordOrDefault(pass);
        return userCollection.find(and(eq("login", login.trim()), eq("password", encryptedPass))).first();
    }

    public String encryptPasswordOrDefault(String password) {
        try {
            return encryptPassword(password);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error al encriptar la contraseña: "+e);
            throw new RuntimeException("Error al encriptar la contraseña"+ e);
        }
    }

    public String encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");  // Asegúrate de que el algoritmo está correcto
        md.update(password.getBytes());
        byte[] bytes = md.digest();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(bytes);
    }

    public boolean insertNewUser(Users newUsers) {
        newUsers.setId(new ObjectId());
        InsertOneResult result = userCollection.insertOne(newUsers);
        return result.wasAcknowledged();
    }

    public boolean modifyUser(Users newUsers) {
        UpdateResult result = userCollection.replaceOne(eq("_id", newUsers.getId()), newUsers);
        return result.wasAcknowledged();
    }

    public boolean deleteUser(Users newUsers) {
        DeleteResult result = userCollection.deleteOne(eq("login", newUsers.getLogin()));
        return result.wasAcknowledged();
    }

    public boolean verifyUser(Users newUsers) {
        if (newUsers.getLogin() == null || newUsers.getLogin().trim().isEmpty()) {
            return false;
        }
        Users existingUsers = userCollection.find(eq("login", newUsers.getLogin().trim())).first();
        return existingUsers == null;
    }

    public boolean verifyEmail(Users newUsers) {
        if (newUsers.getEmail() == null || newUsers.getEmail().trim().isEmpty()) {
            return false;
        }
        String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(newUsers.getEmail().trim());
        if (!matcher.matches()) {
            return false;
        }
        Users existingUsers = userCollection.find(eq("email", newUsers.getEmail().trim())).first();
        return existingUsers == null;
    }
}
