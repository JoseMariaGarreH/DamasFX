package com.example.damasfx.VDataBase;

import com.example.damasfx.Gestion.ScoresManagement;
import com.example.damasfx.Gestion.UserManagement;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DataBase {
    // CONFIGURA INTELLIJ CON LAS VARIABLES DE ENTORNO NECESARIAS PARA ACCEDER A LA BASE DE DATOS
    private static final String MONGO_URI = System.getenv("MONGO_URI");
    private static final String DATABASE_NAME = System.getenv("DB_NAME");
    private static DataBase instance;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private UserManagement userCollection;
    private ScoresManagement scoreCollection;


    private DataBase() {
        if (MONGO_URI == null || DATABASE_NAME == null) {
            System.out.println("ERROR: No se han establecido las variables de entorno necesarias para conectarse"
                    + "a la base de datos MONGODB");
            System.out.println("Consulta la clase modelos/ManagerDB.java para más información.");
            System.exit(1);
        }

        ConnectionString connectionString = new ConnectionString(MONGO_URI);
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();

        mongoClient = MongoClients.create(clientSettings);
        System.out.println("--Conectado a la base de datos");
        mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);

        System.out.println("--Conectando con la colección USUARIOS");
        userCollection = new UserManagement(mongoDatabase);

        System.out.println("--Conectando con la colección PUNTUACIONES");
        scoreCollection = new ScoresManagement(mongoDatabase);
    }
    public UserManagement getUserCollection() {
        return userCollection;
    }
    public ScoresManagement getScoreCollection(){return scoreCollection;};
    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

}
