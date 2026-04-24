package com.example.demo17.controller;

import com.example.demo17.dao.VehicleDAO;
import com.example.demo17.model.Vehicle;
import com.example.demo17.util.AlertHelper;
import com.example.demo17.util.SceneManager;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private TableView<Vehicle>            vehicleTable;
    @FXML private TableColumn<Vehicle, String>  colReg;
    @FXML private TableColumn<Vehicle, String>  colMake;
    @FXML private TableColumn<Vehicle, String>  colModel;
    @FXML private TableColumn<Vehicle, Integer> colYear;
    @FXML private TableColumn<Vehicle, String>  colColor;
    @FXML private TableColumn<Vehicle, String>  colOwner;

    @FXML private TextField         searchField;
    @FXML private ProgressBar       progressBar;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Pagination        pagination;
    @FXML private VBox              scrollContent;
    @FXML private Label             statusLabel;
    @FXML private Button            addVehicleButton;
    @FXML private Button            refreshButton;

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private ObservableList<Vehicle> allVehicles = FXCollections.observableArrayList();
    private static final int ITEMS_PER_PAGE = 5;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupEffects();
        populateScrollPane();
        loadVehicles();
    }

    private void setupTable() {
        colReg.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        colMake.setCellValueFactory(new PropertyValueFactory<>("make"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        vehicleTable.setPlaceholder(new Label("No vehicles found."));
    }

    private void setupEffects() {
        DropShadow shadow = new DropShadow(12, Color.FORESTGREEN);
        addVehicleButton.setEffect(shadow);

        FadeTransition fade = new FadeTransition(Duration.millis(900), refreshButton);
        fade.setFromValue(1.0);
        fade.setToValue(0.4);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();

        progressBar.setProgress(0);
        progressIndicator.setVisible(false);
    }

    private void populateScrollPane() {
        for (int i = 1; i <= 25; i++) {
            Label lbl = new Label(i + ".  Vehicle Reference Entry — LSO-"
                    + String.format("%03d", i) + "-XX");
            lbl.setStyle("-fx-padding: 4 8; -fx-font-size: 13px;");
            scrollContent.getChildren().add(lbl);
        }
    }

    private void loadVehicles() {
        progressIndicator.setVisible(true);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        statusLabel.setText("Loading vehicles...");

        new Thread(() -> {
            try {
                List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
                javafx.application.Platform.runLater(() -> {
                    allVehicles.setAll(vehicles);
                    updatePagination(vehicles);
                    progressBar.setProgress(1.0);
                    progressIndicator.setVisible(false);
                    statusLabel.setText("Loaded " + vehicles.size() + " vehicles.");
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    progressBar.setProgress(0);
                    progressIndicator.setVisible(false);
                    statusLabel.setText("Error loading data.");
                    AlertHelper.showError("Database Error", e.getMessage());
                });
            }
        }).start();
    }

    private void updatePagination(List<Vehicle> vehicles) {
        int pageCount = (int) Math.ceil((double) vehicles.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(Math.max(pageCount, 1));
        pagination.setPageFactory(pageIndex -> {
            int from = pageIndex * ITEMS_PER_PAGE;
            int to   = Math.min(from + ITEMS_PER_PAGE, vehicles.size());
            vehicleTable.setItems(
                    FXCollections.observableArrayList(vehicles.subList(from, to)));
            return vehicleTable;
        });
    }

    // Add this method anywhere in DashboardController class
    @FXML
    private void menuPaginationDemo() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/PaginationDemo.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Pagination Demo - 25 Vehicle Records");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Error", "Could not open demo: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            updatePagination(allVehicles);
            statusLabel.setText("Showing all " + allVehicles.size() + " vehicles.");
            return;
        }
        List<Vehicle> results = vehicleDAO.searchByReg(query);
        vehicleTable.setItems(FXCollections.observableArrayList(results));
        statusLabel.setText("Found " + results.size() + " result(s).");
    }

    @FXML private void handleRefresh()    { loadVehicles(); }
    @FXML private void handleAddVehicle() { SceneManager.switchScene("Vehicle",   "Vehicles"); }

    // ── Menu actions ─────────────────────────────────────────────────────
    @FXML private void menuExit()      { javafx.application.Platform.exit(); }
    @FXML private void menuAdmin()     { SceneManager.switchScene("Admin",     "Admin"); }
    @FXML private void menuVehicles()  { SceneManager.switchScene("Vehicle",   "Vehicles"); }
    @FXML private void menuCustomers() { SceneManager.switchScene("Customer",  "Customers"); }
    @FXML private void menuPolice()    { SceneManager.switchScene("Police",    "Police Module"); }
    @FXML private void menuWorkshop()  { SceneManager.switchScene("Workshop",  "Workshop"); }
    @FXML private void menuAbout() {
        AlertHelper.showInfo("About VIS",
                "Vehicle Identification System\nv1.0 — OOP II\nLimkokwing University");
    }
}
