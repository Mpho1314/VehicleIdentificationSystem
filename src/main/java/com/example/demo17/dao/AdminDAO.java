package com.example.demo17.dao;

import com.example.demo17.database.DatabaseConnection;
import com.example.demo17.model.SystemUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<SystemUser> getAllUsers() {
        List<SystemUser> list = new ArrayList<>();
        String sql = "SELECT * FROM admin_user ORDER BY user_id";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new SystemUser(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("access_code"),
                        rs.getString("role"),
                        rs.getString("access"),
                        rs.getDate("created_at").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            System.err.println("[AdminDAO] getAllUsers: " + e.getMessage());
        }
        return list;
    }

    public String addUser(SystemUser u) {
        String code = u.getRole().substring(0, 3).toUpperCase()
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String sql = "INSERT INTO admin_user(username, password, access_code, role, access) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, code);
            ps.setString(4, u.getRole());
            ps.setString(5, "GRANTED");
            ps.executeUpdate();
            return code;
        } catch (SQLException e) {
            System.err.println("[AdminDAO] addUser: " + e.getMessage());
            return null;
        }
    }

    public boolean grantAccess(int userId) {
        String sql = "UPDATE admin_user SET access = 'GRANTED' WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AdminDAO] grantAccess: " + e.getMessage());
            return false;
        }
    }

    public boolean denyAccess(int userId) {
        String sql = "UPDATE admin_user SET access = 'DENIED' WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AdminDAO] denyAccess: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM admin_user WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AdminDAO] deleteUser: " + e.getMessage());
            return false;
        }
    }

    public SystemUser verifyAccessCode(String code) {
        String sql = "SELECT * FROM admin_user WHERE access_code = ? AND access = 'GRANTED'";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new SystemUser(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("access_code"),
                        rs.getString("role"),
                        rs.getString("access"),
                        rs.getDate("created_at").toLocalDate()
                );
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("[AdminDAO] verifyAccessCode: " + e.getMessage());
        }
        return null;
    }
}