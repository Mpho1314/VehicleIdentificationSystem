package com.example.demo17.dao;

import com.example.demo17.database.DatabaseConnection;
import com.example.demo17.model.PoliceReport;
import com.example.demo17.model.Violation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PoliceDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<PoliceReport> getAllReports() {
        List<PoliceReport> list = new ArrayList<>();
        String sql = "SELECT pr.*, v.registration_number FROM police_report pr " +
                "JOIN vehicle v ON pr.vehicle_id = v.vehicle_id ORDER BY pr.report_date DESC";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                PoliceReport r = new PoliceReport(
                        rs.getInt("report_id"),
                        rs.getInt("vehicle_id"),
                        rs.getDate("report_date").toLocalDate(),
                        rs.getString("report_type"),
                        rs.getString("description"),
                        rs.getString("officer_name")
                );
                r.setRegistrationNumber(rs.getString("registration_number"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("[PoliceDAO] getAllReports: " + e.getMessage());
        }
        return list;
    }

    public boolean addReport(PoliceReport r) {
        String sql = "INSERT INTO police_report(vehicle_id, report_date, report_type, description, officer_name)" +
                " VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt   (1, r.getVehicleId());
            ps.setDate  (2, Date.valueOf(r.getRecordDate()));
            ps.setString(3, r.getReportType());
            ps.setString(4, r.getDescription());
            ps.setString(5, r.getOfficerName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PoliceDAO] addReport: " + e.getMessage());
            return false;
        }
    }

    public List<Violation> getAllViolations() {
        List<Violation> list = new ArrayList<>();
        String sql = "SELECT * FROM view_vehicle_violations ORDER BY violation_date DESC";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Violation vl = new Violation();
                vl.setRegistrationNumber(rs.getString("registration_number"));
                vl.setRecordDate(rs.getDate("violation_date").toLocalDate());
                vl.setViolationType(rs.getString("violation_type"));
                vl.setFineAmount(rs.getDouble("fine_amount"));
                vl.setStatus(rs.getString("status"));
                list.add(vl);
            }
        } catch (SQLException e) {
            System.err.println("[PoliceDAO] getAllViolations: " + e.getMessage());
        }
        return list;
    }

    public boolean addViolation(Violation vl) {
        String sql = "INSERT INTO violation(vehicle_id, violation_date, violation_type, fine_amount, status)" +
                " VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt   (1, vl.getVehicleId());
            ps.setDate  (2, Date.valueOf(vl.getRecordDate()));
            ps.setString(3, vl.getViolationType());
            ps.setDouble(4, vl.getFineAmount());
            ps.setString(5, vl.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PoliceDAO] addViolation: " + e.getMessage());
            return false;
        }
    }

    public boolean updateViolationStatus(int violationId, String status) {
        String sql = "CALL sp_update_violation_status(?, ?)";
        try (CallableStatement cs = getConn().prepareCall(sql)) {
            cs.setInt   (1, violationId);
            cs.setString(2, status);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("[PoliceDAO] updateViolationStatus: " + e.getMessage());
            return false;
        }
    }
}