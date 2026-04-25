package com.example.demo17.controller;

import com.example.demo17.dao.PoliceDAO;
import com.example.demo17.dao.VehicleDAO;
import com.example.demo17.model.PoliceReport;
import com.example.demo17.model.Vehicle;
import com.example.demo17.model.Violation;
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

public class PoliceController implements Initializable {

    // Reports tab
    @FXML private TableView<PoliceReport>           reportTable;
    @FXML private TableColumn<PoliceReport, String> colRepReg;
    @FXML private TableColumn<PoliceReport, String> colRepType;
    @FXML private TableColumn<PoliceReport, String> colRepDate;
    @FXML private TableColumn<PoliceReport, String> colRepOfficer;
    @FXML private TableColumn<PoliceReport, String> colRepDesc;

    @FXML private ComboBox<Vehicle> repVehicleCombo;
    @FXML private ComboBox<String>  repTypeCombo;
    @FXML private TextField         repOfficerField;
    @FXML private TextArea          repDescField;
    @FXML private DatePicker        repDatePicker;

    // Violations tab
    @FXML private TableView<Violation>           violationTable;
    @FXML private TableColumn<Violation, String> colVioReg;
    @FXML private TableColumn<Violation, String> colVioType;
    @FXML private TableColumn<Violation, String> colVioDate;
    @FXML private TableColumn<Violation, Double> colVioFine;
    @FXML private TableColumn<Violation, String> colVioStatus;

    @FXML private ComboBox<Vehicle> vioVehicleCombo;
    @FXML private ComboBox<String>  vioTypeCombo;      // changed from TextField
    @FXML private TextField         vioFineField;
    @FXML private ComboBox<String>  vioStatusCombo;
    @FXML private DatePicker        vioDatePicker;

    @FXML private Label             statusLabel;
    @FXML private ProgressIndicator progressIndicator;

    private final PoliceDAO  policeDAO  = new PoliceDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupReportTable();
        setupViolationTable();
        loadComboData();
        loadReports();
        loadViolations();
        progressIndicator.setVisible(false);

        // Default both date pickers to today and lock them to today only
        repDatePicker.setValue(LocalDate.now());
        vioDatePicker.setValue(LocalDate.now());

        // Disable manual date typing — force today only
        repDatePicker.setEditable(false);
        vioDatePicker.setEditable(false);

        // Restrict dates to today only
        repDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || !date.equals(LocalDate.now()));
            }
        });

        vioDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || !date.equals(LocalDate.now()));
            }
        });

        // Only allow numbers and one decimal point in fine field
        vioFineField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                vioFineField.setText(oldVal);
            }
        });

        // Only allow letters and spaces in officer name field
        repOfficerField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-Z ]*")) {
                repOfficerField.setText(oldVal);
            }
        });
    }

    private void setupReportTable() {
        colRepReg.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        colRepType.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        colRepDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getRecordDate() != null
                        ? d.getValue().getRecordDate().toString() : ""));
        colRepOfficer.setCellValueFactory(new PropertyValueFactory<>("officerName"));
        colRepDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        reportTable.setPlaceholder(new Label("No reports found."));
    }

    private void setupViolationTable() {
        colVioReg.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        colVioType.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        colVioFine.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        colVioStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colVioDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getRecordDate() != null
                        ? d.getValue().getRecordDate().toString() : ""));
        violationTable.setPlaceholder(new Label("No violations found."));
    }

    private void loadComboData() {
        try {
            ObservableList<Vehicle> vehicles =
                    FXCollections.observableArrayList(vehicleDAO.getAllVehicles());
            repVehicleCombo.setItems(vehicles);
            vioVehicleCombo.setItems(vehicles);

            repTypeCombo.setItems(FXCollections.observableArrayList(
                    "Accident", "Theft", "Inspection", "Reckless Driving", "Other"));

            vioTypeCombo.setItems(FXCollections.observableArrayList(
                    "Speeding",
                    "Running Red Light",
                    "Drunk Driving",
                    "No Valid License",
                    "Unregistered Vehicle",
                    "Reckless Driving",
                    "No Seatbelt",
                    "Illegal Parking",
                    "Using Phone While Driving",
                    "Overloading"));

            vioStatusCombo.setItems(FXCollections.observableArrayList("Unpaid", "Paid"));
            vioStatusCombo.setValue("Unpaid");

        } catch (Exception e) {
            AlertHelper.showError("Error", "Could not load vehicles: " + e.getMessage());
        }
    }

    private void loadReports() {
        progressIndicator.setVisible(true);
        new Thread(() -> {
            try {
                List<PoliceReport> list = policeDAO.getAllReports();
                javafx.application.Platform.runLater(() -> {
                    reportTable.setItems(FXCollections.observableArrayList(list));
                    statusLabel.setText(list.size() + " reports loaded.");
                    progressIndicator.setVisible(false);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    AlertHelper.showError("Load Error", e.getMessage());
                });
            }
        }).start();
    }

    private void loadViolations() {
        new Thread(() -> {
            try {
                List<Violation> list = policeDAO.getAllViolations();
                javafx.application.Platform.runLater(() ->
                        violationTable.setItems(FXCollections.observableArrayList(list)));
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        AlertHelper.showError("Load Error", e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void handleSaveReport() {
        try {
            Vehicle   vehicle  = repVehicleCombo.getValue();
            String    type     = repTypeCombo.getValue();
            String    officer  = repOfficerField.getText().trim();
            String    desc     = repDescField.getText().trim();
            LocalDate date     = repDatePicker.getValue();

            // Validation
            if (vehicle == null) {
                AlertHelper.showWarning("Validation", "Please select a vehicle.");
                return;
            }
            if (type == null) {
                AlertHelper.showWarning("Validation", "Please select a report type.");
                return;
            }
            if (officer.isEmpty()) {
                AlertHelper.showWarning("Validation", "Officer name is required.");
                return;
            }
            if (!officer.matches("[a-zA-Z ]+")) {
                AlertHelper.showWarning("Validation",
                        "Officer name must contain letters only.");
                return;
            }
            if (date == null) {
                AlertHelper.showWarning("Validation", "Please select a date.");
                return;
            }
            if (!date.equals(LocalDate.now())) {
                AlertHelper.showWarning("Validation",
                        "Only today's date is allowed for reports.");
                return;
            }

            PoliceReport r = new PoliceReport(
                    0, vehicle.getVehicleId(), date, type, desc, officer);
            boolean ok = policeDAO.addReport(r);
            statusLabel.setText(ok ? "Report saved successfully." : "Save failed.");
            if (ok) { clearReportForm(); loadReports(); }

        } catch (Exception e) {
            AlertHelper.showError("Error", e.getMessage());
        }
    }

    @FXML
    private void handleSaveViolation() {
        try {
            Vehicle   vehicle = vioVehicleCombo.getValue();
            String    type    = vioTypeCombo.getValue();
            String    fineStr = vioFineField.getText().trim();
            String    status  = vioStatusCombo.getValue();
            LocalDate date    = vioDatePicker.getValue();

            // Validation
            if (vehicle == null) {
                AlertHelper.showWarning("Validation", "Please select a vehicle.");
                return;
            }
            if (type == null || type.isEmpty()) {
                AlertHelper.showWarning("Validation", "Please select a violation type.");
                return;
            }
            if (fineStr.isEmpty()) {
                AlertHelper.showWarning("Validation", "Fine amount is required.");
                return;
            }
            if (!fineStr.matches("\\d+(\\.\\d+)?")) {
                AlertHelper.showWarning("Validation",
                        "Fine amount must be numeric only (LSL).");
                return;
            }
            if (date == null) {
                AlertHelper.showWarning("Validation", "Please select a date.");
                return;
            }
            if (!date.equals(LocalDate.now())) {
                AlertHelper.showWarning("Validation",
                        "Only today's date is allowed for violations.");
                return;
            }

            double fine = Double.parseDouble(fineStr);
            Violation vl = new Violation(
                    0, vehicle.getVehicleId(), date, type, fine, status);
            boolean ok = policeDAO.addViolation(vl);
            statusLabel.setText(ok
                    ? "Violation saved. Fine: LSL " + String.format("%.2f", fine)
                    : "Save failed.");
            if (ok) { clearViolationForm(); loadViolations(); }

        } catch (NumberFormatException e) {
            AlertHelper.showError("Input Error", "Fine amount must be a valid number.");
        } catch (Exception e) {
            AlertHelper.showError("Error", e.getMessage());
        }
    }

    private void clearReportForm() {
        repVehicleCombo.setValue(null);
        repTypeCombo.setValue(null);
        repOfficerField.clear();
        repDescField.clear();
        repDatePicker.setValue(LocalDate.now());
    }

    private void clearViolationForm() {
        vioVehicleCombo.setValue(null);
        vioTypeCombo.setValue(null);
        vioFineField.clear();
        vioStatusCombo.setValue("Unpaid");
        vioDatePicker.setValue(LocalDate.now());
    }

    @FXML private void goBack() { SceneManager.switchScene("Dashboard", "Dashboard"); }
}