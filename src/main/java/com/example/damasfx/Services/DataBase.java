package com.example.damasfx.Services;

import com.example.damasfx.Utils.UserManagement;
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
    // Variables de entorno para la URI y el nombre de la base de datos
    private static final String MONGO_URI = System.getenv("MONGO_URI");
    private static final String DATABASE_NAME = System.getenv("DB_NAME");
    private static DataBase instance; // Instancia única de la clase DataBase
    // Cliente de MongoDB y base de datos
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private UserManagement userCollection; // Gestión de usuarios

    // Constructor privado para la implementación del patrón singleton
    private DataBase() {
        // Verifica si las variables de entorno necesarias están configuradas
        if (MONGO_URI == null || DATABASE_NAME == null) {
            System.out.println("ERROR: No se han establecido las variables de entorno necesarias para conectarse"
                    + " a la base de datos MONGODB");
            System.out.println("Consulta la clase modelos/ManagerDB.java para más información.");
            System.exit(1);
        }

        // Configuración de la conexión a MongoDB
        ConnectionString connectionString = new ConnectionString(MONGO_URI);
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();

        // Crea el cliente de MongoDB con las configuraciones establecidas
        mongoClient = MongoClients.create(clientSettings);
        System.out.println("--Conectado a la base de datos");
        // Obtiene la base de datos con el nombre especificado
        mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);

        // Conecta con la colección de usuarios
        System.out.println("--Conectando con la colección USUARIOS");
        userCollection = new UserManagement(mongoDatabase);
    }

    // Método para obtener la colección de usuarios
    public UserManagement getUserCollection() {
        return userCollection;
    }

    // Método para obtener la instancia única de la clase DataBase
    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }
}
