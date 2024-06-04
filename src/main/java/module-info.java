module com.example.damasfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires com.google.gson;
    requires java.desktop;
    requires org.apache.logging.log4j;
    requires java.prefs;

    opens com.example.damasfx to javafx.fxml;
    exports com.example.damasfx;
    exports com.example.damasfx.Controladores;
    exports com.example.damasfx.Modelo;
    opens com.example.damasfx.Controladores to javafx.fxml;
    exports com.example.damasfx.VDataBase;
    opens com.example.damasfx.VDataBase to javafx.fxml;
    exports com.example.damasfx.Enumerados;
    exports com.example.damasfx.Gestion;
}