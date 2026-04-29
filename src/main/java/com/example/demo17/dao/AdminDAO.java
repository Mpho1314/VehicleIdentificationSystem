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
        String sql = "SELECT * FROM admin_user ORDER BY admin_id";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new SystemUser(
                        rs.getInt("admin_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("access_code"),
                        rs.getString("role"),
                        rs.getString("access"),
                        rs.getDate("created_at") != null
                                ? rs.getDate("created_at").toLocalDate() : null
                ));
            }
        } catch (SQLException e) {
            System.err.println("[AdminDAO] getAllUsers: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM admin_user WHERE username = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[AdminDAO] usernameExists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public String addUser(SystemUser u) {
        if (u.getRole() == null || u.getRole().isEmpty()) {
            System.err.println("[AdminDAO] addUser: role is null or empty");
            return null;
        }

        // Generate access code e.g. WOR-A1B2C3
        String code = u.getRole().substring(0, 3).toUpperCase()
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // Generate user_id as text e.g. USR-A1B2C3
        String userId = "USR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        String sql = "INSERT INTO admin_user(username, password, user_id, access_code, role, access) "
                + "VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, userId);
            ps.setString(4, code);
            ps.setString(5, u.getRole());
            ps.setString(6, "GRANTED");
            ps.executeUpdate();
            return code;
        } catch (SQLException e) {
            System.err.println("[AdminDAO] addUser FAILED:");
            System.err.println("  SQLState : " + e.getSQLState());
            System.err.println("  ErrorCode: " + e.getErrorCode());
            System.err.println("  Message  : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean grantAccess(int adminId) {
        String sql = "UPDATE admin_user SET access = 'GRANTED' WHERE admin_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AdminDAO] grantAccess: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean denyAccess(int adminId) {
        String sql = "UPDATE admin_user SET access = 'DENIED' WHERE admin_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AdminDAO] denyAccess: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int adminId) {
        String sql = "DELETE FROM admin_user WHERE admin_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AdminDAO] deleteUser: " + e.getMessage());
            e.printStackTrace();
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
                        rs.getInt("admin_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("access_code"),
                        rs.getString("role"),
                        rs.getString("access"),
                        rs.getDate("created_at") != null
                                ? rs.getDate("created_at").toLocalDate() : null
                );
            }
        } catch (SQLException e) {
            System.err.println("[AdminDAO] verifyAccessCode: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}