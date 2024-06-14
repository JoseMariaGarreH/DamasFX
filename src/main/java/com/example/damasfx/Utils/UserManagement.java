package com.example.damasfx.Utils;

import com.example.damasfx.Controllers.StartController;
import com.example.damasfx.Models.Users;
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
import java.util.prefs.Preferences;


import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;


public class UserManagement {
    private static final Logger logger = LogManager.getLogger(StartController.class);
    private static final String TABLE_NAME = "USERS_SIGNED"; // Tabla de base de datos de MongoDB
    private static MongoCollection<Users> userCollection; // Colección de usuarios de la tabla hecha en mongoDB
    // Declaración de variables para representar usuarios individuales.
    private Users firstUser;
    private Users secondUser;
    private Users currentUser;

    private static final String USERNAME_KEY = "loggedInUser"; // Definición de una clave para el nombre de usuario utilizado para el inicio de sesión.
    private final Preferences prefs; // Declaración de una variable final que almacena las preferencias del usuario.


    public UserManagement(){
        prefs = Preferences.userNodeForPackage(UserManagement.class);
    }

    // Inicializamos cargando la colección que se recoge de la tabla 'USERS_SIGNED'
    public UserManagement(MongoDatabase db) {
        userCollection = db.getCollection(TABLE_NAME, Users.class);
        prefs = Preferences.userNodeForPackage(UserManagement.class);
    }
    // Salvamos el usuario logueado
    public void saveLoggedInUser(String username) {
        prefs.put(USERNAME_KEY, username);
    }

    // Recogemos el usuario que esta logueado en la aplicación
    public String getLoggedInUser() {
        return prefs.get(USERNAME_KEY, null);
    }

    // Eliminamos el inicio de sesión del usuario que está dentro de la aplicación
    public void clearLoggedInUser() {
        prefs.remove(USERNAME_KEY);
    }


    public Users getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(Users firstUser) {
        this.firstUser = firstUser;
    }

    public Users getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(Users secondUser) {
        this.secondUser = secondUser;
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Users currentUser) {
        this.currentUser = currentUser;
    }

    // Método que recoge la lista de usuarios registrados
    public ObservableList<Users> getUserCollection() {
        return userCollection.find().into(FXCollections.observableArrayList());
    }

    // Método el cual coge el usuario por el nombre, segun el nombre de la cuenta del usuario
    public Users getUserById(String userId) {
        Users user;
        user = userCollection.find(eq("login", userId)).first();
        return user;
    }

    // Método el cual encuentra el usuario registrado en la base de datos, segun el nombre de la cuenta y la contraseñ
    public Users userLogin(String login, String pass) {
        return userCollection.find(and(eq("login", login.trim()), eq("password", pass))).first();
    }

    public Users searchEmail(String email){
        return userCollection.find(eq("email", email.trim())).first();
    }

    // Inserta usuario en Mongo DB
    public boolean insertNewUser(Users newUsers) {
        newUsers.setId(new ObjectId());
        InsertOneResult result = userCollection.insertOne(newUsers);
        return result.wasAcknowledged();
    }

    // Modifica el usuario en Mongo DB, según su id único
    public boolean modifyUser(Users newUsers) {
        UpdateResult result = userCollection.replaceOne(eq("_id", newUsers.getId()), newUsers);
        return result.wasAcknowledged();
    }

    // Elimina al usuario según el nombre de la cuenta del usuario
    public boolean deleteUser(Users newUsers) {
        DeleteResult result = userCollection.deleteOne(eq("login", newUsers.getLogin()));
        return result.wasAcknowledged();
    }

    // Verifica si el nombre de la cuenta del usuario existe en la base de datos
    public boolean verifyUser(Users newUsers) {
        Users existingUsers = userCollection.find(eq("login", newUsers.getLogin().trim())).first();
        return existingUsers == null;
    }

    // Verifica si el nombre del email del usuario existe en la base de datos
    public boolean verifyEmail(Users newUsers) {
        Users existingUsers = userCollection.find(eq("email", newUsers.getEmail().trim())).first();
        return existingUsers == null;
    }
}
