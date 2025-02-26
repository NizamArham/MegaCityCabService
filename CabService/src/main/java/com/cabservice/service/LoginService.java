package com.cabservice.service;

import com.cabservice.util.DatabaseConnection;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Base64;

public class LoginService {

    private static final String SECRET_KEY = "b8e3f9a4b6d7c2e1f5g9h0j3k2m1n8o7";

    public String authenticateUser(String email, String password) {
        if (validateUserCredentials(email, password)) {
            String accountType = getUserAccountType(email);

            if (accountType != null) {
                String token = generateJWT(email, accountType);
                return String.format("{\"status\": \"success\", \"acc_type\": \"%s\", \"token\": \"%s\"}", accountType, token);
            } else {
                return "{\"status\": \"error\", \"message\": \"Account type not found\"}";
            }
        } else {
            return "{\"status\": \"error\", \"message\": \"Invalid credentials\"}";
        }
    }

    private boolean validateUserCredentials(String email, String password) {
        boolean isValid = false;

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT password_hash FROM login_info WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password_hash");
                    System.out.println("Stored Password: " + storedPassword);
                    System.out.println("Entered Password (Hashed): " + hashPassword(password));

                    isValid = hashPassword(password).equals(storedPassword);
                    System.out.println("Password Match: " + isValid);
                } else {
                    System.out.println("No user found with email: " + email);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isValid;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
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
        String payload = Base64.getEncoder().encodeToString(
                String.format("{\"email\":\"%s\",\"account_type\":\"%s\",\"exp\":%d}",
                        email, accountType, System.currentTimeMillis() / 1000 + 3600).getBytes()
        );
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
