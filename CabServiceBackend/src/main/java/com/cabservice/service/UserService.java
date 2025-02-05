package com.cabservice.service;

import com.cabservice.model.User;
import com.cabservice.util.DatabaseConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class UserService {

    public boolean isUserExists(String email, String nic, String tp) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkQuery = "SELECT email, nic, tp FROM users WHERE email = ? OR nic = ? OR tp = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, email);
                checkStmt.setString(2, nic);
                checkStmt.setString(3, tp);
                ResultSet rs = checkStmt.executeQuery();
                return rs.next();  // Returns true if user exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createUser(User user) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertUser = "INSERT INTO users (first_name, last_name, nic, tp, email, account_type, account_status) VALUES (?, ?, ?, ?, ?, 'customer', 'active')";
            try (PreparedStatement stmt = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getFirstName());
                stmt.setString(2, user.getLastName());
                stmt.setString(3, user.getNic());
                stmt.setString(4, user.getTp());
                stmt.setString(5, user.getEmail());
                stmt.executeUpdate();

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    return insertLoginInfo(userId, user.getEmail(), user.getPassword());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean insertLoginInfo(int userId, String email, String password) {
        String hashedPassword = hashPassword(password);
        String insertLogin = "INSERT INTO login_info (user_id, email, password_hash) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(insertLogin)) {
            stmt.setInt(1, userId);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
