package com.example.demo17.controller;

import com.example.demo17.dao.InsuranceDAO;
import com.example.demo17.dao.VehicleDAO;
import com.example.demo17.model.Vehicle;
import com.example.demo17.util.AlertHelper;
import com.example.demo17.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class InsuranceController implements Initializable {

    @FXML private TableView<InsuranceDAO.Insurance>           insuranceTable;
    @FXML private TableColumn<InsuranceDAO.Insurance, String> colReg;
    @FXML private TableColumn<InsuranceDAO.Insurance, String> colPolicy;
    @FXML private TableColumn<InsuranceDAO.Insurance, String> colProvider;
    @FXML private TableColumn<InsuranceDAO.Insurance, String> colStart;
    @FXML private TableColumn<InsuranceDAO.Insurance, String> colEnd;
    @FXML private TableColumn<InsuranceDAO.Insurance, Double> colPremium;
    @FXML private TableColumn<InsuranceDAO.Insurance, String> colStatus;

    @FXML private ComboBox<Vehicle>  vehicleCombo;
    @FXML private TextField          policyField;
    @FXML private TextField          providerField;
    @FXML private DatePicker         startDatePicker;
    @FXML private DatePicker         endDatePicker;
    @FXML private TextField          premiumField;
    @FXML private ComboBox<String>   statusCombo;
    @FXML private Label              statusLabel;
    @FXML private ProgressIndicator  progressIndicator;
    @FXML private ProgressBar        progressBar;

    private final InsuranceDAO insuranceDAO = new InsuranceDAO();
    private final VehicleDAO   vehicleDAO   = new VehicleDAO();
    private InsuranceDAO.Insurance selectedInsurance = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupForm();
        loadInsurance();
        progressIndicator.setVisible(false);

        // Select insurance from table
        insuranceTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> selectedInsurance = sel);
    }

    private void setupTable() {
        colReg.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        colPolicy.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
        colProvider.setCellValueFactory(new PropertyValueFactory<>("provider"));
        colStart.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStartDateStr()));
        colEnd.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEndDateStr()));
        colPremium.setCellValueFactory(new PropertyValueFactory<>("premiumAmount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        insuranceTable.setPlaceholder(new Label("No insurance records found."));
    }

    private void setupForm() {
        // Load vehicles into combo
        try {
            vehicleCombo.setItems(
                    FXCollections.observableArrayList(vehicleDAO.getAllVehicles()));
        } catch (Exception e) {
            System.err.println("Error loading vehicles: " + e.getMessage());
        }

        // Status combo
        statusCombo.setItems(FXCollections.observableArrayList("ACTIVE", "EXPIRED", "CANCELLED"));
        statusCombo.setValue("ACTIVE");

        // Start date — today only
        startDatePicker.setValue(LocalDate.now());
        startDatePicker.setEditable(false);
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || !date.equals(LocalDate.now()));
            }
        });

        // End date — must be future date only
        endDatePicker.setValue(LocalDate.now().plusMonths(12));
        endDatePicker.setEditable(false);
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now().plusDays(1)));
            }
        });

        // Premium — numbers only
        premiumField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                premiumField.setText(oldVal);
            }
        });

        // Provider — letters and spaces only
        providerField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-Z ]*")) {
                providerField.setText(oldVal);
            }
        });
    }

    private void loadInsurance() {
        progressIndicator.setVisible(true);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        new Thread(() -> {
            try {
                List<InsuranceDAO.Insurance> list = insuranceDAO.getAllInsurance();
                javafx.application.Platform.runLater(() -> {
                    insuranceTable.setItems(FXCollections.observableArrayList(list));
                    statusLabel.setText(list.size() + " insurance records loaded.");
                    progressIndicator.setVisible(false);
                    progressBar.setProgress(1.0);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    progressBar.setProgress(0);
                    AlertHelper.showError("Load Error", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleSave() {
        try {
            Vehicle   vehicle  = vehicleCombo.getValue();
            String    policy   = policyField.getText().trim();
            String    provider = providerField.getText().trim();
            LocalDate start    = startDatePicker.getValue();
            LocalDate end      = endDatePicker.getValue();
            String    premium  = premiumField.getText().trim();
            String    status   = statusCombo.getValue();

            // Validations
            if (vehicle == null) {
                AlertHelper.showWarning("Validation", "Please select a vehicle.");
                return;
            }
            if (policy.isEmpty()) {
                AlertHelper.showWarning("Validation", "Policy number is required.");
                return;
            }
            if (provider.isEmpty()) {
                AlertHelper.showWarning("Validation", "Provider name is required.");
                return;
            }
            if (!provider.matches("[a-zA-Z ]+")) {
                AlertHelper.showWarning("Validation",
                        "Provider name must contain letters only.");
                return;
            }
            if (premium.isEmpty()) {
                AlertHelper.showWarning("Validation", "Premium amount is required.");
                return;
            }
            if (!premium.matches("\\d+(\\.\\d+)?")) {
                AlertHelper.showWarning("Validation",
                        "Premium amount must be numeric (LSL).");
                return;
            }
            if (start == null || end == null) {
                AlertHelper.showWarning("Validation", "Both dates are required.");
                return;
            }
            if (!start.equals(LocalDate.now())) {
                AlertHelper.showWarning("Validation",
                        "Start date must be today's date only.");
                return;
            }
            if (!end.isAfter(LocalDate.now())) {
                AlertHelper.showWarning("Validation",
                        "End date must be a future date.");
                return;
            }
            if (insuranceDAO.policyExists(policy)) {
                AlertHelper.showWarning("Duplicate Policy",
                        "Policy number '" + policy + "' already exists.");
                return;
            }

            double premiumAmount = Double.parseDouble(premium);
            boolean ok = insuranceDAO.addInsurance(
                    vehicle.getVehicleId(), policy, provider,
                    start, end, premiumAmount, status);

            if (ok) {
                statusLabel.setText("Insurance record saved. Premium: LSL "
                        + String.format("%.2f", premiumAmount));
                clearForm();
                loadInsurance();
            } else {
                AlertHelper.showError("Error", "Failed to save insurance record.");
            }

        } catch (Exception e) {
            AlertHelper.showError("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedInsurance == null) {
            AlertHelper.showWarning("No Selection",
                    "Please select an insurance record to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete policy '" + selectedInsurance.getPolicyNumber() + "'?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                boolean ok = insuranceDAO.deleteInsurance(
                        selectedInsurance.getInsuranceId());
                statusLabel.setText(ok ? "Record deleted." : "Delete failed.");
                if (ok) { selectedInsurance = null; loadInsurance(); }
            }
        });
    }

    @FXML
    private void handleClear() { clearForm(); }

    private void clearForm() {
        vehicleCombo.setValue(null);
        policyField.clear();
        providerField.clear();
        premiumField.clear();
        statusCombo.setValue("ACTIVE");
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusMonths(12));
    }

    @FXML private void goBack() {
        SceneManager.switchScene("Dashboard", "Dashboard");
    }
}