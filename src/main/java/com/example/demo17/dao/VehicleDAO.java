package com.example.demo17.dao;

import com.example.demo17.database.DatabaseConnection;
import com.example.demo17.model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean addVehicle(Vehicle v) {
        String sql = "CALL sp_add_vehicle(?, ?, ?, ?, ?, ?)";
        try (CallableStatement cs = getConn().prepareCall(sql)) {
            cs.setString(1, v.getRegistrationNumber());
            cs.setString(2, v.getMake());
            cs.setString(3, v.getModel());
            cs.setInt   (4, v.getYear());
            cs.setString(5, v.getColor());
            cs.setInt   (6, v.getOwnerId());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] addVehicle: " + e.getMessage());
            return false;
        }
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM view_vehicle_owner ORDER BY vehicle_id";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Vehicle v = mapRow(rs);
                v.setOwnerName(rs.getString("owner_name"));
                list.add(v);
            }
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] getAllVehicles: " + e.getMessage());
        }
        return list;
    }

    public List<Vehicle> searchByReg(String reg) {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM view_vehicle_owner WHERE LOWER(registration_number) LIKE LOWER(?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, "%" + reg + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vehicle v = mapRow(rs);
                v.setOwnerName(rs.getString("owner_name"));
                list.add(v);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] searchByReg: " + e.getMessage());
        }
        return list;
    }

    public boolean updateVehicle(Vehicle v) {
        String sql = "UPDATE vehicle SET make=?, model=?, year=?, color=? WHERE vehicle_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, v.getMake());
            ps.setString(2, v.getModel());
            ps.setInt   (3, v.getYear());
            ps.setString(4, v.getColor());
            ps.setInt   (5, v.getVehicleId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] updateVehicle: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteVehicle(int vehicleId) {
        String sql = "DELETE FROM vehicle WHERE vehicle_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] deleteVehicle: " + e.getMessage());
            return false;
        }
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleId(rs.getInt("vehicle_id"));
        v.setRegistrationNumber(rs.getString("registration_number"));
        v.setMake(rs.getString("make"));
        v.setModel(rs.getString("model"));
        v.setYear(rs.getInt("year"));
        v.setColor(rs.getString("color"));
        return v;
    }
}

