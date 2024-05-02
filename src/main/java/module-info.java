module com.example.damasfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires com.google.gson;
    requires java.desktop;

    opens com.example.damasfx to javafx.fxml;
    exports com.example.damasfx;
    exports com.example.damasfx.Controlador;
    exports com.example.damasfx.Modelo;
    opens com.example.damasfx.Controlador to javafx.fxml;
    exports com.example.damasfx.Vista;
    opens com.example.damasfx.Vista to javafx.fxml;
}