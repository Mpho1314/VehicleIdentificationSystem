package com.example.demo17.dao;

import com.example.demo17.database.DatabaseConnection;
import com.example.demo17.model.Vehicle;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InsuranceDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public static class Insurance {
        private int insuranceId;
        private int vehicleId;
        private String registrationNumber;
        private String policyNumber;
        private String provider;
        private LocalDate startDate;
        private LocalDate endDate;
        private double premiumAmount;
        private String status;

        public Insurance(int insuranceId, int vehicleId, String registrationNumber,
                         String policyNumber, String provider,
                         LocalDate startDate, LocalDate endDate,
                         double premiumAmount, String status) {
            this.insuranceId        = insuranceId;
            this.vehicleId          = vehicleId;
            this.registrationNumber = registrationNumber;
            this.policyNumber       = policyNumber;
            this.provider           = provider;
            this.startDate          = startDate;
            this.endDate            = endDate;
            this.premiumAmount      = premiumAmount;
            this.status             = status;
        }

        public int    getInsuranceId()        { return insuranceId; }
        public int    getVehicleId()           { return vehicleId; }
        public String getRegistrationNumber()  { return registrationNumber; }
        public String getPolicyNumber()        { return policyNumber; }
        public String getProvider()            { return provider; }
        public LocalDate getStartDate()        { return startDate; }
        public LocalDate getEndDate()          { return endDate; }
        public double getPremiumAmount()       { return premiumAmount; }
        public String getStatus()              { return status; }

        public String getStartDateStr() {
            return startDate != null ? startDate.toString() : "";
        }
        public String getEndDateStr() {
            return endDate != null ? endDate.toString() : "";
        }
    }

    public List<Insurance> getAllInsurance() {
        List<Insurance> list = new ArrayList<>();
        String sql = "SELECT i.*, v.registration_number " +
                "FROM insurance i " +
                "LEFT JOIN vehicle v ON i.vehicle_id = v.vehicle_id " +
                "ORDER BY i.insurance_id";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Insurance(
                        rs.getInt("insurance_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        rs.getString("policy_number"),
                        rs.getString("provider"),
                        rs.getDate("start_date") != null
                                ? rs.getDate("start_date").toLocalDate() : null,
                        rs.getDate("end_date") != null
                                ? rs.getDate("end_date").toLocalDate() : null,
                        rs.getDouble("premium_amount"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[InsuranceDAO] getAllInsurance: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean addInsurance(int vehicleId, String policyNumber, String provider,
                                LocalDate startDate, LocalDate endDate,
                                double premiumAmount, String status) {
        String sql = "INSERT INTO insurance(vehicle_id, policy_number, provider, " +
                "start_date, end_date, premium_amount, status) " +
                "VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.setString(2, policyNumber);
            ps.setString(3, provider);
            ps.setDate(4, Date.valueOf(startDate));
            ps.setDate(5, Date.valueOf(endDate));
            ps.setDouble(6, premiumAmount);
            ps.setString(7, status);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InsuranceDAO] addInsurance FAILED: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteInsurance(int insuranceId) {
        String sql = "DELETE FROM insurance WHERE insurance_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, insuranceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InsuranceDAO] deleteInsurance: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean policyExists(String policyNumber) {
        String sql = "SELECT COUNT(*) FROM insurance WHERE policy_number = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, policyNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[InsuranceDAO] policyExists: " + e.getMessage());
        }
        return false;
    }
}