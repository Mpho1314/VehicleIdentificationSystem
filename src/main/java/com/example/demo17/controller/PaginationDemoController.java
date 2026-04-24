package com.example.demo17.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class PaginationDemoController {

    @FXML private Pagination pagination;
    @FXML private ScrollPane scrollPane;
    @FXML private ProgressBar progressBar;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Button demoButton;
    @FXML private Label statusLabel;

    private String[] dummyItems = new String[25]; // 25 items (more than 20 required)

    @FXML
    public void initialize() {
        // Initialize dummy data
        for (int i = 0; i < 25; i++) {
            dummyItems[i] = "Vehicle Record " + (i + 1) + " - Registration: LSO-0" + (i+1) + "-XX";
        }

        setupPagination();
        setupProgressIndicators();
        setupVisualEffects();
    }

    private void setupPagination() {
        int itemsPerPage = 5;
        int totalPages = (int) Math.ceil(dummyItems.length / (double) itemsPerPage);

        pagination.setPageCount(totalPages);
        pagination.setPageFactory(pageIndex -> {
            VBox vbox = new VBox(10);
            int start = pageIndex * itemsPerPage;
            int end = Math.min(start + itemsPerPage, dummyItems.length);

            for (int i = start; i < end; i++) {
                Label itemLabel = new Label(dummyItems[i]);
                itemLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
                vbox.getChildren().add(itemLabel);
            }

            // Update progress as user paginates
            double progress = (pageIndex + 1.0) / totalPages;
            progressBar.setProgress(progress);
            progressIndicator.setProgress(progress);

            return vbox;
        });
    }

    private void setupProgressIndicators() {
        // Initial progress
        progressBar.setProgress(0.0);
        progressIndicator.setProgress(0.0);

        // Simulate loading
        new Thread(() -> {
            for (int i = 0; i <= 100; i++) {
                final int progress = i;
                javafx.application.Platform.runLater(() -> {
                    progressBar.setProgress(progress / 100.0);
                    progressIndicator.setProgress(progress / 100.0);
                    if (progress == 100) {
                        statusLabel.setText("✓ Data loaded successfully!");
                    }
                });
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            }
        }).start();
    }

    private void setupVisualEffects() {
        // DropShadow Effect (7 marks requirement)
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10.0);
        dropShadow.setOffsetX(5.0);
        dropShadow.setOffsetY(5.0);
        dropShadow.setColor(Color.GRAY);
        demoButton.setEffect(dropShadow);

        // FadeTransition Effect (7 marks requirement)
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), demoButton);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.3);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
        fadeTransition.play();

        // Button action
        demoButton.setOnAction(e -> {
            statusLabel.setText("✓ Button clicked! Effect working!");
        });
    }

    // ← ADDED THIS METHOD (was missing)
    @FXML
    private void handleExit() {
        javafx.application.Platform.exit();
    }
}