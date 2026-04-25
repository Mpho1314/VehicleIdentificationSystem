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

    private static String cssPath;

    public static String getCssPath() { return cssPath; }

    @Override
    public void start(Stage stage) {
        try {
            URL cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl != null) {
                cssPath = cssUrl.toExternalForm();
                System.out.println("✓ CSS found: " + cssPath);
            } else {
                System.err.println("✗ CSS NOT FOUND");
            }

            URL fxmlUrl = getClass().getResource("/fxml/Login.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            javafx.geometry.Rectangle2D screen =
                    javafx.stage.Screen.getPrimary().getVisualBounds();

            Scene scene = new Scene(root, screen.getWidth(), screen.getHeight());

            if (cssPath != null) {
                scene.getStylesheets().add(cssPath);
            }

            stage.setTitle("Vehicle Identification System");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setResizable(true);
            stage.show();

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