package com.example.demo17.controller;

import com.example.demo17.dao.CustomerDAO;
import com.example.demo17.dao.VehicleDAO;
import com.example.demo17.model.Customer;
import com.example.demo17.model.Vehicle;
import com.example.demo17.util.AlertHelper;
import com.example.demo17.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class VehicleController implements Initializable {

    // Table columns
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> colReg;
    @FXML private TableColumn<Vehicle, String> colMake;
    @FXML private TableColumn<Vehicle, String> colModel;
    @FXML private TableColumn<Vehicle, Integer> colYear;
    @FXML private TableColumn<Vehicle, String> colColor;
    @FXML private TableColumn<Vehicle, String> colOwner;

    // Search and Pagination
    @FXML private TextField searchField;
    @FXML private Pagination pagination;
    @FXML private ProgressBar progressBar;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label statusLabel;
    @FXML private Label formStatusLabel;

    // Quick Reference ScrollPane
    @FXML private VBox scrollContent;

    // Form fields
    @FXML private TextField regField;
    @FXML private TextField makeField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private TextField colorField;
    @FXML private ComboBox<Customer> ownerCombo;

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private ObservableList<Vehicle> allVehicles = FXCollections.observableArrayList();
    private Vehicle selectedVehicle = null;
    private static final int ITEMS_PER_PAGE = 6;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupComboBox();
        setupProgressIndicators();
        populateQuickReference();
        loadVehicles();

        // Add selection listener
        vehicleTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected != null) {
                        populateForm(selected);
                    }
                });
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

    private void setupComboBox() {
        try {
            ownerCombo.setItems(FXCollections.observableArrayList(customerDAO.getAllCustomers()));
        } catch (Exception e) {
            System.err.println("Error loading owners: " + e.getMessage());
        }
    }

    private void setupProgressIndicators() {
        progressBar.setProgress(0);
        progressIndicator.setVisible(false);
    }

    private void populateQuickReference() {
        for (int i = 1; i <= 25; i++) {
            Label lbl = new Label(i + ". Vehicle Reference — LSO-" + String.format("%03d", i) + "-XX");
            lbl.setStyle("-fx-padding: 4 8; -fx-font-size: 11px; -fx-text-fill: #2c3e50;");
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
            int to = Math.min(from + ITEMS_PER_PAGE, vehicles.size());
            if (from < vehicles.size()) {
                vehicleTable.setItems(FXCollections.observableArrayList(vehicles.subList(from, to)));
            } else {
                vehicleTable.setItems(FXCollections.observableArrayList());
            }
            return vehicleTable;
        });
    }

    private void populateForm(Vehicle v) {
        selectedVehicle = v;
        regField.setText(v.getRegistrationNumber());
        makeField.setText(v.getMake());
        modelField.setText(v.getModel());
        yearField.setText(String.valueOf(v.getYear()));
        colorField.setText(v.getColor());

        // Set owner in combo box
        if (v.getOwnerId() > 0) {
            for (Customer c : ownerCombo.getItems()) {
                if (c.getCustomerId() == v.getOwnerId()) {
                    ownerCombo.setValue(c);
                    break;
                }
            }
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

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        loadVehicles();
        statusLabel.setText("Search cleared. Showing all vehicles.");
    }

    @FXML
    private void handleSave() {
        try {
            String reg = regField.getText().trim();
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            String yearStr = yearField.getText().trim();
            String color = colorField.getText().trim();
            Customer owner = ownerCombo.getValue();

            if (reg.isEmpty() || make.isEmpty() || model.isEmpty() || yearStr.isEmpty()) {
                AlertHelper.showWarning("Validation", "Please fill all required fields.");
                return;
            }

            int year = Integer.parseInt(yearStr);
            if (year < 1900 || year > 2100) {
                AlertHelper.showWarning("Validation", "Enter a valid year (1900–2100).");
                return;
            }

            Vehicle v = new Vehicle();
            v.setRegistrationNumber(reg);
            v.setMake(make);
            v.setModel(model);
            v.setYear(year);
            v.setColor(color);
            v.setOwnerId(owner != null ? owner.getCustomerId() : 0);

            boolean ok;
            if (selectedVehicle != null && selectedVehicle.getVehicleId() > 0) {
                v.setVehicleId(selectedVehicle.getVehicleId());
                ok = vehicleDAO.updateVehicle(v);
                formStatusLabel.setText(ok ? "✓ Vehicle updated successfully." : "Update failed.");
            } else {
                ok = vehicleDAO.addVehicle(v);
                formStatusLabel.setText(ok ? "✓ Vehicle added successfully." : "Add failed.");
            }

            if (ok) {
                handleClear();
                loadVehicles();
            }

        } catch (NumberFormatException e) {
            AlertHelper.showError("Input Error", "Year must be a number.");
        } catch (Exception e) {
            AlertHelper.showError("Save Error", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedVehicle == null) {
            AlertHelper.showWarning("No Selection", "Please select a vehicle to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete vehicle " + selectedVehicle.getRegistrationNumber() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                boolean ok = vehicleDAO.deleteVehicle(selectedVehicle.getVehicleId());
                formStatusLabel.setText(ok ? "✓ Vehicle deleted." : "Delete failed.");
                if (ok) {
                    handleClear();
                    loadVehicles();
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        selectedVehicle = null;
        regField.clear();
        makeField.clear();
        modelField.clear();
        yearField.clear();
        colorField.clear();
        ownerCombo.setValue(null);
        vehicleTable.getSelectionModel().clearSelection();
        formStatusLabel.setText("");
    }

    @FXML
    private void goBack() {
        SceneManager.switchScene("Dashboard", "Dashboard");
    }
}