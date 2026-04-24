package com.example.demo17.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String HOST     = "localhost";
    private static final String PORT     = "5432";
    private static final String DATABASE = "vehicle_system";  // ← CHANGED from "postgres"
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "131422"; // ← Change to your PostgreSQL password

    private static final String URL =
            "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE
                    + "?sslmode=disable";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {}

    public static DatabaseConnection getInstance() {
        if (instance == null) instance = new DatabaseConnection();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("[DB] Connected successfully to " + DATABASE);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL driver not found: " + e.getMessage(), e);
        } catch (SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
            throw e;
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing: " + e.getMessage());
        }
    }
}