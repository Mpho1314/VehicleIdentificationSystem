module com.example.demo17 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.demo17 to javafx.fxml;
    opens com.example.demo17.controller to javafx.fxml;
    opens com.example.demo17.model to javafx.base;

    exports com.example.demo17;
    exports com.example.demo17.controller;
    exports com.example.demo17.model;
    exports com.example.demo17.dao;
    exports com.example.demo17.database;
    exports com.example.demo17.util;
}