package com.example.demo17.controller;

import com.example.demo17.dao.AdminDAO;
import com.example.demo17.model.SystemUser;
import com.example.demo17.util.AlertHelper;
import com.example.demo17.util.SceneManager;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML private TableView<SystemUser>            userTable;
    @FXML private TableColumn<SystemUser, Integer> colId;
    @FXML private TableColumn<SystemUser, String>  colUsername;
    @FXML private TableColumn<SystemUser, String>  colCode;
    @FXML private TableColumn<SystemUser, String>  colRole;
    @FXML private TableColumn<SystemUser, String>  colAccess;
    @FXML private TableColumn<SystemUser, String>  colDate;

    @FXML private TextField        usernameField;
    @FXML private PasswordField    passwordField;
    @FXML private ComboBox<String> roleCombo;

    @FXML private Button grantButton;
    @FXML private Button denyButton;
    @FXML private Button addButton;

    @FXML private Label             statusLabel;
    @FXML private Label             codeLabel;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private ProgressBar       progressBar;

    private final AdminDAO adminDAO = new AdminDAO();
    private ObservableList<SystemUser> userList = FXCollections.observableArrayList();
    private SystemUser selectedUser = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupEffects();
        setupValidation();

        // Use Platform.runLater to ensure ComboBox is ready
        Platform.runLater(() -> {
            roleCombo.setItems(FXCollections.observableArrayList(
                    "WORKSHOP", "CUSTOMER", "POLICE", "INSURANCE"));
            roleCombo.getSelectionModel().selectFirst();
        });

        progressIndicator.setVisible(false);
        progressBar.setProgress(0);

        userTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> {
                    selectedUser = sel;
                    if (sel != null) {
                        codeLabel.setText("Access Code: " + sel.getAccessCode()
                                + "  |  Role: " + sel.getRole()
                                + "  |  Status: " + sel.getAccess());
                    }
                });

        loadUsers();
    }

    private void setupValidation() {
        // Username: letters, numbers, underscore only
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-Z0-9_]*")) {
                usernameField.setText(oldVal);
            }
        });
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("accessCode"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colAccess.setCellValueFactory(new PropertyValueFactory<>("access"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        userTable.setRowFactory(tv -> new TableRow<SystemUser>() {
            @Override
            protected void updateItem(SystemUser user, boolean empty) {
                super.updateItem(user, empty);
                if (user == null || empty) {
                    setStyle("");
                } else if ("DENIED".equals(user.getAccess())) {
                    setStyle("-fx-background-color: #ffcdd2;");
                } else {
                    setStyle("-fx-background-color: #c8e6c9;");
                }
            }
        });

        userTable.setPlaceholder(new Label("No users found."));
    }

    private void setupEffects() {
        DropShadow grantShadow = new DropShadow(12, Color.GREEN);
        grantButton.setEffect(grantShadow);

        DropShadow denyShadow = new DropShadow(12, Color.RED);
        denyButton.setEffect(denyShadow);

        FadeTransition fade = new FadeTransition(Duration.millis(1000), addButton);
        fade.setFromValue(1.0);
        fade.setToValue(0.5);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();
    }

    private void loadUsers() {
        progressIndicator.setVisible(true);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        new Thread(() -> {
            try {
                List<SystemUser> list = adminDAO.getAllUsers();
                Platform.runLater(() -> {
                    userList.setAll(list);
                    userTable.setItems(userList);
                    statusLabel.setText(list.size() + " users loaded.");
                    progressIndicator.setVisible(false);
                    progressBar.setProgress(1.0);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    progressBar.setProgress(0);
                    AlertHelper.showError("Load Error", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleAddUser() {
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String role     = roleCombo.getValue();

            // Validations
            if (username.isEmpty()) {
                AlertHelper.showWarning("Validation", "Username is required.");
                return;
            }
            if (username.length() < 3) {
                AlertHelper.showWarning("Validation",
                        "Username must be at least 3 characters.");
                return;
            }
            if (password.isEmpty()) {
                AlertHelper.showWarning("Validation", "Password is required.");
                return;
            }
            if (password.length() < 4) {
                AlertHelper.showWarning("Validation",
                        "Password must be at least 4 characters.");
                return;
            }
            if (role == null || role.isEmpty()) {
                AlertHelper.showWarning("Validation",
                        "Please select a Role / Module.");
                return;
            }

            // Check duplicate before insert
            if (adminDAO.usernameExists(username)) {
                AlertHelper.showWarning("Duplicate Username",
                        "The username '" + username + "' already exists.\n"
                                + "Please choose a different username.");
                return;
            }

            SystemUser u = new SystemUser();
            u.setUsername(username);
            u.setPassword(password);
            u.setRole(role);

            String generatedCode = adminDAO.addUser(u);
            if (generatedCode != null) {
                codeLabel.setText("User created! Access Code: " + generatedCode
                        + " — Share this with the user.");
                statusLabel.setText("User '" + username + "' added successfully.");
                usernameField.clear();
                passwordField.clear();
                loadUsers();
            } else {
                AlertHelper.showError("Database Error",
                        "Failed to insert user.\n"
                                + "Check the IntelliJ console for the exact SQL error.");
            }

        } catch (Exception e) {
            AlertHelper.showError("Unexpected Error",
                    e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGrant() {
        if (selectedUser == null) {
            AlertHelper.showWarning("No Selection", "Please select a user first.");
            return;
        }
        boolean ok = adminDAO.grantAccess(selectedUser.getUserId());
        statusLabel.setText(ok
                ? "Access GRANTED for: " + selectedUser.getUsername()
                : "Failed to grant access.");
        if (ok) loadUsers();
    }

    @FXML
    private void handleDeny() {
        if (selectedUser == null) {
            AlertHelper.showWarning("No Selection", "Please select a user first.");
            return;
        }
        boolean ok = adminDAO.denyAccess(selectedUser.getUserId());
        statusLabel.setText(ok
                ? "Access DENIED for: " + selectedUser.getUsername()
                : "Failed to deny access.");
        if (ok) loadUsers();
    }

    @FXML
    private void handleDelete() {
        if (selectedUser == null) {
            AlertHelper.showWarning("No Selection",
                    "Please select a user to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete user '" + selectedUser.getUsername() + "'?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                boolean ok = adminDAO.deleteUser(selectedUser.getUserId());
                statusLabel.setText(ok ? "User deleted." : "Delete failed.");
                if (ok) {
                    selectedUser = null;
                    codeLabel.setText("Select a user to view their access code");
                    loadUsers();
                }
            }
        });
    }

    @FXML
    private void goBack() {
        SceneManager.switchScene("Dashboard", "Dashboard");
    }
}