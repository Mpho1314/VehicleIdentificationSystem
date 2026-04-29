package com.example.demo17.controller;

import com.example.demo17.util.AlertHelper;
import com.example.demo17.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Dashboard loaded.");
    }

    @FXML private void menuAdmin()     { SceneManager.switchScene("Admin",     "Admin"); }
    @FXML private void menuVehicles()  { SceneManager.switchScene("Vehicle",   "Vehicles"); }
    @FXML private void menuCustomers() { SceneManager.switchScene("Customer",  "Customers"); }
    @FXML private void menuPolice()    { SceneManager.switchScene("Police",    "Police Module"); }
    @FXML private void menuWorkshop()  { SceneManager.switchScene("Workshop",  "Workshop"); }
    @FXML private void menuInsurance() { SceneManager.switchScene("Insurance", "Insurance"); }
    @FXML private void menuLogout()    { SceneManager.switchScene("Login",     "Login"); }
    @FXML private void menuAbout() {
        AlertHelper.showInfo("About VIS",
                "Vehicle Identification System\nv1.0 — OOP II\nLimkokwing University");
    }
}