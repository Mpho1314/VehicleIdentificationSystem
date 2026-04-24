package com.example.demo17.dao;

import com.example.demo17.database.DatabaseConnection;
import com.example.demo17.model.ServiceRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<ServiceRecord> getAllServices() {
        List<ServiceRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM view_service_history ORDER BY service_date DESC";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ServiceRecord sr = new ServiceRecord();
                sr.setRegistrationNumber(rs.getString("registration_number"));
                sr.setRecordDate(rs.getDate("service_date").toLocalDate());
                sr.setServiceType(rs.getString("service_type"));
                sr.setDescription(rs.getString("description"));
                sr.setCost(rs.getDouble("cost"));
                list.add(sr);
            }
        } catch (SQLException e) {
            System.err.println("[ServiceDAO] getAllServices: " + e.getMessage());
        }
        return list;
    }

    public boolean addService(ServiceRecord sr) {
        String sql = "INSERT INTO service_record(vehicle_id, service_date, service_type, description, cost)" +
                " VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt   (1, sr.getVehicleId());
            ps.setDate  (2, Date.valueOf(sr.getRecordDate()));
            ps.setString(3, sr.getServiceType());
            ps.setString(4, sr.getDescription());
            ps.setDouble(5, sr.getCost());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ServiceDAO] addService: " + e.getMessage());
            return false;
        }
    }
}
