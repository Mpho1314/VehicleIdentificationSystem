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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class VehicleController implements Initializable {

    @FXML private TableView<Vehicle>            vehicleTable;
    @FXML private TableColumn<Vehicle, String>  colReg;
    @FXML private TableColumn<Vehicle, String>  colMake;
    @FXML private TableColumn<Vehicle, String>  colModel;
    @FXML private TableColumn<Vehicle, Integer> colYear;
    @FXML private TableColumn<Vehicle, String>  colColor;
    @FXML private TableColumn<Vehicle, String>  colOwner;

    @FXML private TextField          regField;
    @FXML private TextField          makeField;
    @FXML private TextField          modelField;
    @FXML private TextField          yearField;
    @FXML private TextField          colorField;
    @FXML private ComboBox<Customer> ownerCombo;
    @FXML private Label              statusLabel;
    @FXML private ProgressIndicator  progressIndicator;

    private final VehicleDAO  vehicleDAO  = new VehicleDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();
    private Vehicle selectedVehicle = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colReg.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        colMake.setCellValueFactory(new PropertyValueFactory<>("make"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        vehicleTable.setPlaceholder(new Label("No vehicles loaded."));

        vehicleTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> populateForm(sel));

        progressIndicator.setVisible(false);
        loadOwners();
        loadVehicles();
    }

    private void loadOwners() {
        try {
            ownerCombo.setItems(FXCollections.observableArrayList(customerDAO.getAllCustomers()));
        } catch (Exception e) {
            AlertHelper.showError("Error", "Could not load owners: " + e.getMessage());
        }
    }

    private void loadVehicles() {
        progressIndicator.setVisible(true);
        new Thread(() -> {
            try {
                List<Vehicle> list = vehicleDAO.getAllVehicles();
                javafx.application.Platform.runLater(() -> {
                    vehicleList.setAll(list);
                    vehicleTable.setItems(vehicleList);
                    statusLabel.setText(list.size() + " vehicles loaded.");
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

    private void populateForm(Vehicle v) {
        if (v == null) return;
        selectedVehicle = v;
        regField.setText(v.getRegistrationNumber());
        makeField.setText(v.getMake());
        modelField.setText(v.getModel());
        yearField.setText(String.valueOf(v.getYear()));
        colorField.setText(v.getColor());
    }

    @FXML
    private void handleSave() {
        try {
            String reg   = regField.getText().trim();
            String make  = makeField.getText().trim();
            String model = modelField.getText().trim();
            String yearS = yearField.getText().trim();
            String color = colorField.getText().trim();
            Customer owner = ownerCombo.getValue();

            if (reg.isEmpty() || make.isEmpty() || model.isEmpty() || yearS.isEmpty()) {
                AlertHelper.showWarning("Validation", "Please fill all required fields.");
                return;
            }
            int year = Integer.parseInt(yearS);
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
                statusLabel.setText(ok ? "Vehicle updated." : "Update failed.");
            } else {
                ok = vehicleDAO.addVehicle(v);
                statusLabel.setText(ok ? "Vehicle added." : "Add failed.");
            }
            if (ok) { handleClear(); loadVehicles(); }

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
                statusLabel.setText(ok ? "Deleted." : "Delete failed.");
                if (ok) { handleClear(); loadVehicles(); }
            }
        });
    }

    @FXML
    private void handleClear() {
        selectedVehicle = null;
        regField.clear(); makeField.clear(); modelField.clear();
        yearField.clear(); colorField.clear(); ownerCombo.setValue(null);
        vehicleTable.getSelectionModel().clearSelection();
        statusLabel.setText("");
    }

    @FXML private void goBack() { SceneManager.switchScene("Dashboard", "Dashboard"); }
}
