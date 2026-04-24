package com.example.demo17.controller;

import com.example.demo17.dao.ServiceDAO;
import com.example.demo17.dao.VehicleDAO;
import com.example.demo17.model.ServiceRecord;
import com.example.demo17.model.Vehicle;
import com.example.demo17.util.AlertHelper;
import com.example.demo17.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class WorkshopController implements Initializable {

    @FXML private TableView<ServiceRecord>           serviceTable;
    @FXML private TableColumn<ServiceRecord, String> colReg;
    @FXML private TableColumn<ServiceRecord, String> colType;
    @FXML private TableColumn<ServiceRecord, String> colDate;
    @FXML private TableColumn<ServiceRecord, Double> colCost;
    @FXML private TableColumn<ServiceRecord, String> colDesc;

    @FXML private ComboBox<Vehicle> vehicleCombo;
    @FXML private ComboBox<String>  serviceTypeCombo;
    @FXML private TextField         costField;
    @FXML private TextArea          descField;
    @FXML private DatePicker        datePicker;
    @FXML private Label             statusLabel;
    @FXML private ProgressIndicator progressIndicator;

    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colReg.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        colType.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        colDate.setCellValueFactory(sr -> new SimpleStringProperty(
                sr.getValue().getRecordDate() != null
                        ? sr.getValue().getRecordDate().toString() : ""));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        serviceTable.setPlaceholder(new Label("No service records found."));

        serviceTypeCombo.setItems(FXCollections.observableArrayList(
                "Oil Change", "Tyre Rotation", "Brake Service", "Full Service",
                "Battery Replacement", "Engine Repair", "Transmission", "Other"));

        datePicker.setValue(LocalDate.now());
        progressIndicator.setVisible(false);
        loadVehicles();
        loadServices();
    }

    private void loadVehicles() {
        try {
            vehicleCombo.setItems(
                    FXCollections.observableArrayList(vehicleDAO.getAllVehicles()));
        } catch (Exception e) {
            AlertHelper.showError("Error", "Could not load vehicles: " + e.getMessage());
        }
    }

    private void loadServices() {
        progressIndicator.setVisible(true);
        new Thread(() -> {
            try {
                List<ServiceRecord> list = serviceDAO.getAllServices();
                javafx.application.Platform.runLater(() -> {
                    serviceTable.setItems(FXCollections.observableArrayList(list));
                    statusLabel.setText(list.size() + " records loaded.");
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

    @FXML
    private void handleSave() {
        try {
            Vehicle  vehicle = vehicleCombo.getValue();
            String   type    = serviceTypeCombo.getValue();
            String   costStr = costField.getText().trim();
            String   desc    = descField.getText().trim();
            LocalDate date   = datePicker.getValue();

            if (vehicle == null || type == null || costStr.isEmpty()) {
                AlertHelper.showWarning("Validation",
                        "Vehicle, service type, and cost are required.");
                return;
            }
            double cost = Double.parseDouble(costStr);
            ServiceRecord sr = new ServiceRecord(
                    0, vehicle.getVehicleId(), date, type, desc, cost);
            boolean ok = serviceDAO.addService(sr);
            statusLabel.setText(ok ? "Service record saved." : "Save failed.");
            if (ok) { handleClear(); loadServices(); }

        } catch (NumberFormatException e) {
            AlertHelper.showError("Input Error", "Cost must be a valid number.");
        } catch (Exception e) {
            AlertHelper.showError("Error", e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        vehicleCombo.setValue(null);
        serviceTypeCombo.setValue(null);
        costField.clear();
        descField.clear();
        datePicker.setValue(LocalDate.now());
        statusLabel.setText("");
    }

    @FXML private void goBack() { SceneManager.switchScene("Dashboard", "Dashboard"); }
}

