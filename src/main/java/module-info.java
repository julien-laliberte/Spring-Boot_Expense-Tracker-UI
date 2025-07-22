module dursahn.expensetrackerui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;
    requires static lombok;
    requires MaterialFX;
    requires java.prefs;
    requires javafx.base;
    requires java.security.sasl;


    opens dursahn.expensetrackerui to javafx.fxml;
    opens dursahn.expensetrackerui.controllers to javafx.fxml;
    opens dursahn.expensetrackerui.models to com.google.gson, javafx.base;
    exports dursahn.expensetrackerui;
}