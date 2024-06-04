package com.example.damasfx.Gestion;

import com.example.damasfx.Modelo.Scores;
import com.example.damasfx.Modelo.Users;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bson.types.ObjectId;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public class ScoresManagement {
    private static final String TABLE_NAME = "SCORES";
    private static MongoCollection<Scores> scoresCollection;
    private static MongoCollection<Users> userCollection;

    public ScoresManagement(MongoDatabase db) {
        scoresCollection = db.getCollection(TABLE_NAME, Scores.class);
        userCollection = db.getCollection("USERS_SIGNED", Users.class);
    }

    public ObservableList<Scores> getScoreCollection() {
        ObservableList<Scores> scores = FXCollections.observableArrayList(scoresCollection.find().into(new ArrayList<>()));
        scores.forEach(score -> {
            Users user = userCollection.find(eq("_id", new ObjectId(score.getUserId()))).first();
            if (user != null) {
                score.setUserName(user.getLogin());
            }
        });
        return scores;
    }

    public void insertNewScore(Scores newScore) {
        InsertOneResult result = scoresCollection.insertOne(newScore);
        result.wasAcknowledged();
    }

    public void modifyScore(Scores newScore) {
        UpdateResult result = scoresCollection.replaceOne(eq("_id", newScore.getId()), newScore);
        result.wasAcknowledged();
    }

    public void deleteScore(Users newUsers) {
        DeleteResult result = scoresCollection.deleteOne(eq("userName",newUsers.getLogin()));
        result.wasAcknowledged();
    }
}
