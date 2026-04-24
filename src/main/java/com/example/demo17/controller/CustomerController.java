package com.example.demo17.controller;

import com.example.demo17.dao.CustomerDAO;
import com.example.demo17.model.Customer;
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

public class CustomerController implements Initializable {

    @FXML private TableView<Customer>            customerTable;
    @FXML private TableColumn<Customer, Integer> colId;
    @FXML private TableColumn<Customer, String>  colName;
    @FXML private TableColumn<Customer, String>  colPhone;
    @FXML private TableColumn<Customer, String>  colEmail;
    @FXML private TableColumn<Customer, String>  colAddress;

    @FXML private TextField  nameField;
    @FXML private TextField  phoneField;
    @FXML private TextField  emailField;
    @FXML private TextArea   addressField;
    @FXML private Label      statusLabel;
    @FXML private ProgressIndicator progressIndicator;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private Customer selectedCustomer = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerTable.setPlaceholder(new Label("No customers found."));

        customerTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> populateForm(sel));

        progressIndicator.setVisible(false);
        loadCustomers();
    }

    private void loadCustomers() {
        progressIndicator.setVisible(true);
        new Thread(() -> {
            try {
                List<Customer> list = customerDAO.getAllCustomers();
                javafx.application.Platform.runLater(() -> {
                    customerList.setAll(list);
                    customerTable.setItems(customerList);
                    statusLabel.setText(list.size() + " customers loaded.");
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

    private void populateForm(Customer c) {
        if (c == null) return;
        selectedCustomer = c;
        nameField.setText(c.getName());
        phoneField.setText(c.getPhone());
        emailField.setText(c.getEmail());
        addressField.setText(c.getAddress());
    }

    @FXML
    private void handleSave() {
        try {
            String name    = nameField.getText().trim();
            String phone   = phoneField.getText().trim();
            String email   = emailField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                AlertHelper.showWarning("Validation", "Name and Email are required.");
                return;
            }

            Customer c = new Customer(0, name, address, phone, email);
            boolean ok;
            if (selectedCustomer != null && selectedCustomer.getCustomerId() > 0) {
                c.setCustomerId(selectedCustomer.getCustomerId());
                ok = customerDAO.updateCustomer(c);
                statusLabel.setText(ok ? "Customer updated." : "Update failed.");
            } else {
                ok = customerDAO.addCustomer(c);
                statusLabel.setText(ok ? "Customer added." : "Add failed.");
            }
            if (ok) { handleClear(); loadCustomers(); }

        } catch (Exception e) {
            AlertHelper.showError("Save Error", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedCustomer == null) {
            AlertHelper.showWarning("No Selection", "Select a customer to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete customer " + selectedCustomer.getName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                boolean ok = customerDAO.deleteCustomer(selectedCustomer.getCustomerId());
                statusLabel.setText(ok ? "Deleted." : "Delete failed.");
                if (ok) { handleClear(); loadCustomers(); }
            }
        });
    }

    @FXML
    private void handleClear() {
        selectedCustomer = null;
        nameField.clear(); phoneField.clear();
        emailField.clear(); addressField.clear();
        customerTable.getSelectionModel().clearSelection();
        statusLabel.setText("");
    }

    @FXML private void goBack() { SceneManager.switchScene("Dashboard", "Dashboard"); }
}
