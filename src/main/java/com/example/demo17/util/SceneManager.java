package com.example.demo17.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlName, String title) {
        try {
            // Load FXML from /fxml/ folder
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/fxml/" + fxmlName + ".fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Load CSS from /css/ folder
            var cssUrl = SceneManager.class.getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Navigation Error", "Could not load " + fxmlName);
        }
    }
}