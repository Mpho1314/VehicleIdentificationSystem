module com.example.demo17 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;

    opens com.example.demo17 to javafx.fxml;
    opens com.example.demo17.controller to javafx.fxml;
    opens com.example.demo17.model to javafx.base;
    opens com.example.demo17.util to javafx.fxml;
    opens com.example.demo17.dao to javafx.base;

    exports com.example.demo17;
    exports com.example.demo17.controller;
    exports com.example.demo17.model;
    exports com.example.demo17.util;
    exports com.example.demo17.dao;
}