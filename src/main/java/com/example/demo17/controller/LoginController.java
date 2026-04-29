package com.example.demo17.controller;

import com.example.demo17.database.DatabaseConnection;
import com.example.demo17.util.AlertHelper;
import com.example.demo17.util.SceneManager;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
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
        DropShadow shadow = new DropShadow(12, Color.web("#1b4332"));
        loginButton.setEffect(shadow);

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
            AlertHelper.showWarning("Input Error",
                    "Please enter username and password.");
            return;
        }

        loadingIndicator.setVisible(true);
        loginButton.setDisable(true);
        statusLabel.setText("Authenticating...");

        new Thread(() -> {
            AuthResult result = authenticate(username, password);

            Platform.runLater(() -> {
                loadingIndicator.setVisible(false);
                loginButton.setDisable(false);

                if (result == null) {
                    failLogin("Invalid username or password.");
                    return;
                }

                if (!result.isApproved) {
                    failLogin("Access denied. Contact your administrator.");
                    return;
                }

                statusLabel.setStyle("-fx-text-fill: #2d6a4f;");
                statusLabel.setText("Login successful! Loading...");

                switch (result.role.toUpperCase()) {
                    case "ADMIN":
                        SceneManager.switchScene("Dashboard", "Dashboard");
                        break;
                    case "WORKSHOP":
                        SceneManager.switchScene("Workshop", "Workshop");
                        break;
                    case "CUSTOMER":
                        SceneManager.switchScene("Customer", "Customers");
                        break;
                    case "POLICE":
                        SceneManager.switchScene("Police", "Police Module");
                        break;
                    case "INSURANCE":
                        SceneManager.switchScene("Insurance", "Insurance");
                        break;
                    default:
                        SceneManager.switchScene("Dashboard", "Dashboard");
                        break;
                }
            });
        }).start();
    }

    private void failLogin(String message) {
        statusLabel.setStyle("-fx-text-fill: #c0392b;");
        statusLabel.setText(message);
        AlertHelper.showError("Login Failed", message);
        passwordField.clear();
        passwordField.requestFocus();
    }

    private AuthResult authenticate(String username, String password) {
        String sql = "SELECT role, access FROM admin_user " +
                "WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role   = rs.getString("role");
                String access = rs.getString("access");
                boolean approved = "GRANTED".equalsIgnoreCase(access);
                return new AuthResult(role, approved);
            }
        } catch (SQLException e) {
            System.err.println("[LoginController] Auth error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static class AuthResult {
        String  role;
        boolean isApproved;
        AuthResult(String role, boolean isApproved) {
            this.role       = role;
            this.isApproved = isApproved;
        }
    }
}