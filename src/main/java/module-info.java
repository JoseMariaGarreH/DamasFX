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
    requires java.mail;

    opens com.example.damasfx to javafx.fxml;
    exports com.example.damasfx;
    exports com.example.damasfx.Controllers;
    exports com.example.damasfx.Models;
    opens com.example.damasfx.Controllers to javafx.fxml;
    exports com.example.damasfx.Services;
    opens com.example.damasfx.Services to javafx.fxml;
    exports com.example.damasfx.Enums;
    exports com.example.damasfx.Utils;
}