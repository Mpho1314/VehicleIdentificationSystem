package com.example.demo17;

import com.example.demo17.database.DatabaseConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Debug: Print all resources in the root
            System.out.println("=== DEBUG: Resource Location ===");
            URL rootUrl = getClass().getResource("/");
            System.out.println("Root URL: " + rootUrl);

            // Check if CSS exists
            URL cssUrl = getClass().getResource("/css/style.css");
            System.out.println("CSS URL: " + cssUrl);

            if (cssUrl == null) {
                System.err.println("CSS NOT FOUND! Trying to list files...");
                // Try to list what's in the resources folder
                java.io.File file = new java.io.File("target/classes/css");
                if (file.exists() && file.isDirectory()) {
                    System.out.println("Files in target/classes/css:");
                    for (String f : file.list()) {
                        System.out.println("  - " + f);
                    }
                }
            } else {
                System.out.println("CSS found at: " + cssUrl.toExternalForm());
            }

            // Load Login FXML
            URL fxmlUrl = getClass().getResource("/fxml/Login.fxml");
            System.out.println("FXML URL: " + fxmlUrl);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Create scene
            Scene scene = new Scene(root, 500, 400);

            // Apply CSS
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("✓ CSS added to scene");
            }

            stage.setTitle("Vehicle Identification System");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            System.out.println("=== Application Started ===");

        } catch (Exception e) {
            System.err.println("✗ Failed to start: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        DatabaseConnection.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}