package com.example.demo17.dao;

import com.example.demo17.database.DatabaseConnection;
import com.example.demo17.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean addCustomer(Customer c) {
        String sql = "INSERT INTO customer(name, address, phone, email) VALUES(?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getAddress());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] addCustomer: " + e.getMessage());
            return false;
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customer ORDER BY customer_id";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getAllCustomers: " + e.getMessage());
        }
        return list;
    }

    public boolean updateCustomer(Customer c) {
        String sql = "UPDATE customer SET name=?, address=?, phone=?, email=? WHERE customer_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getAddress());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getEmail());
            ps.setInt   (5, c.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] updateCustomer: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCustomer(int id) {
        String sql = "DELETE FROM customer WHERE customer_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] deleteCustomer: " + e.getMessage());
            return false;
        }
    }
}
