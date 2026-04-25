package com.example.demo17.util;

import com.example.demo17.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SceneManager {

    public static void switchScene(String fxmlName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/fxml/" + fxmlName + ".fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) Window.getWindows()
                    .stream()
                    .filter(Window::isShowing)
                    .findFirst()
                    .orElseThrow();

            Scene scene = new Scene(root,
                    stage.getScene().getWidth(),
                    stage.getScene().getHeight());

            String css = MainApp.getCssPath();
            if (css != null) {
                scene.getStylesheets().add(css);
            }

            stage.setTitle("VIS — " + title);
            stage.setScene(scene);
            stage.setMaximized(true);

        } catch (Exception e) {
            AlertHelper.showError("Navigation Error",
                    "Could not open " + fxmlName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}