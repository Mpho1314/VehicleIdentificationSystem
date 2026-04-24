package com.example.demo17.controller;

import com.example.demo17.database.DatabaseConnection;
import com.example.demo17.util.AlertHelper;
import com.example.demo17.util.SceneManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField         usernameField;
    @FXML private PasswordField     passwordField;
    @FXML private Button            loginButton;
    @FXML private Label             statusLabel;
    @FXML private ProgressIndicator loadingIndicator;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // DropShadow effect on login button
        DropShadow shadow = new DropShadow(10, Color.DODGERBLUE);
        loginButton.setEffect(shadow);

        // FadeTransition — continuously fades in and out
        FadeTransition fade = new FadeTransition(Duration.millis(1000), loginButton);
        fade.setFromValue(1.0);
        fade.setToValue(0.5);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();

        loadingIndicator.setVisible(false);
        statusLabel.setText("");
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            AlertHelper.showWarning("Input Error", "Please enter username and password.");
            return;
        }

        loadingIndicator.setVisible(true);
        statusLabel.setText("Authenticating...");

        new Thread(() -> {
            boolean success = authenticate(username, password);
            javafx.application.Platform.runLater(() -> {
                loadingIndicator.setVisible(false);
                if (success) {
                    statusLabel.setText("Login successful!");
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    SceneManager.setPrimaryStage(stage);
                    SceneManager.switchScene("Dashboard", "Dashboard");
                } else {
                    statusLabel.setText("Invalid credentials.");
                    AlertHelper.showError("Login Failed", "Invalid username or password.");
                }
            });
        }).start();
    }

    private boolean authenticate(String username, String password) {
        String sql = "SELECT COUNT(*) FROM admin_user WHERE username=? AND password=?";
        try (PreparedStatement ps =
                     DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[LoginController] Auth error: " + e.getMessage());
        }
        return false;
    }
}
