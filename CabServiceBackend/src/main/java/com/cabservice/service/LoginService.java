package com.cabservice.service;

import com.cabservice.model.User;
import com.cabservice.util.DatabaseConnection;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;

public class LoginService {

    private static final String SECRET_KEY = "b8e3f9a4b6d7c2e1f5g9h0j3k2m1n8o7";

    public String authenticateUser(User user) {
        if (validateUserCredentials(user)) {
            String accountType = getUserAccountType(user.getEmail());

            if (accountType != null) {
                String token = generateJWT(user.getEmail(), accountType);
                return "{\"status\": \"success\", \"acc_type\": \"" + accountType + "\", \"token\": \"" + token + "\"}";
            } else {
                return "{\"status\": \"error\", \"message\": \"Invalid account type\"}";
            }
        } else {
            return "{\"status\": \"error\", \"message\": \"Invalid credentials\"}";
        }
    }

    private boolean validateUserCredentials(User user) {
        boolean isValid = false;

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT password_hash FROM login_info WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, user.getEmail());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password_hash");
                    isValid = checkPassword(user.getPassword(), storedHashedPassword);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isValid;
    }

    private boolean checkPassword(String enteredPassword, String storedHashedPassword) {
        String enteredPasswordHash = hashPassword(enteredPassword);
        return enteredPasswordHash.equals(storedHashedPassword);
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

    private String getUserAccountType(String email) {
        String accountType = null;

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT account_type FROM users WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    accountType = rs.getString("account_type");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accountType;
    }

    private String generateJWT(String email, String accountType) {
        String header = Base64.getEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String payload = Base64.getEncoder().encodeToString(("{\"email\":\"" + email + "\",\"account_type\":\"" + accountType + "\",\"exp\":" + (System.currentTimeMillis() / 1000 + 3600) + "}").getBytes());
        String signature = hmacSHA256(header + "." + payload, SECRET_KEY);

        return header + "." + payload + "." + signature;
    }

    private String hmacSHA256(String data, String key) {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC SHA256 signature", e);
        }
    }
}
