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

    // ── Reports tab ──────────────────────────────────────────────────────────
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

    // ── Violations tab ───────────────────────────────────────────────────────
    @FXML private TableView<Violation>           violationTable;
    @FXML private TableColumn<Violation, String> colVioReg;
    @FXML private TableColumn<Violation, String> colVioType;
    @FXML private TableColumn<Violation, String> colVioDate;
    @FXML private TableColumn<Violation, Double> colVioFine;
    @FXML private TableColumn<Violation, String> colVioStatus;

    @FXML private ComboBox<Vehicle> vioVehicleCombo;
    @FXML private TextField         vioTypeField;
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
        repDatePicker.setValue(LocalDate.now());
        vioDatePicker.setValue(LocalDate.now());
    }

    private void setupReportTable() {
        colRepReg.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        colRepType.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        colRepDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getRecordDate() != null ? d.getValue().getRecordDate().toString() : ""));
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
                d.getValue().getRecordDate() != null ? d.getValue().getRecordDate().toString() : ""));
        violationTable.setPlaceholder(new Label("No violations found."));
    }

    private void loadComboData() {
        try {
            ObservableList<Vehicle> vehicles =
                    FXCollections.observableArrayList(vehicleDAO.getAllVehicles());
            repVehicleCombo.setItems(vehicles);
            vioVehicleCombo.setItems(vehicles);
            repTypeCombo.setItems(FXCollections.observableArrayList(
                    "Accident", "Theft", "Inspection", "Other"));
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
            Vehicle  vehicle  = repVehicleCombo.getValue();
            String   type     = repTypeCombo.getValue();
            String   officer  = repOfficerField.getText().trim();
            String   desc     = repDescField.getText().trim();
            LocalDate date    = repDatePicker.getValue();

            if (vehicle == null || type == null || officer.isEmpty()) {
                AlertHelper.showWarning("Validation",
                        "Vehicle, type, and officer name are required.");
                return;
            }
            PoliceReport r = new PoliceReport(
                    0, vehicle.getVehicleId(), date, type, desc, officer);
            boolean ok = policeDAO.addReport(r);
            statusLabel.setText(ok ? "Report saved." : "Save failed.");
            if (ok) { clearReportForm(); loadReports(); }

        } catch (Exception e) {
            AlertHelper.showError("Error", e.getMessage());
        }
    }

    @FXML
    private void handleSaveViolation() {
        try {
            Vehicle  vehicle = vioVehicleCombo.getValue();
            String   type    = vioTypeField.getText().trim();
            String   fineStr = vioFineField.getText().trim();
            String   status  = vioStatusCombo.getValue();
            LocalDate date   = vioDatePicker.getValue();

            if (vehicle == null || type.isEmpty() || fineStr.isEmpty()) {
                AlertHelper.showWarning("Validation",
                        "Vehicle, type, and fine amount are required.");
                return;
            }
            double fine = Double.parseDouble(fineStr);
            Violation vl = new Violation(
                    0, vehicle.getVehicleId(), date, type, fine, status);
            boolean ok = policeDAO.addViolation(vl);
            statusLabel.setText(ok ? "Violation saved." : "Save failed.");
            if (ok) { clearViolationForm(); loadViolations(); }

        } catch (NumberFormatException e) {
            AlertHelper.showError("Input Error", "Fine amount must be a number.");
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
        vioTypeField.clear();
        vioFineField.clear();
        vioStatusCombo.setValue("Unpaid");
        vioDatePicker.setValue(LocalDate.now());
    }

    @FXML private void goBack() { SceneManager.switchScene("Dashboard", "Dashboard"); }
}


