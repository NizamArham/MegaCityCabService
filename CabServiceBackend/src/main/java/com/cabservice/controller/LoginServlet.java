package com.cabservice.controller;

import com.cabservice.util.DatabaseConnection;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String SECRET_KEY = "b8e3f9a4b6d7c2e1f5g9h0j3k2m1n8o7";

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        String requestData = requestBody.toString();
        String email = extractJsonValue(requestData, "email");
        String password = extractJsonValue(requestData, "password");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (validateUserCredentials(email, password)) {
            String accType = getUserAccountType(email);

            if (accType != null) {
                String token = generateJWT(email, accType); // Generate JWT
                out.write("{\"status\": \"success\", \"acc_type\": \"" + accType + "\", \"token\": \"" + token + "\"}");
            } else {
                out.write("{\"status\": \"error\", \"message\": \"Invalid account type\"}");
            }
        } else {
            out.write("{\"status\": \"error\", \"message\": \"Invalid credentials\"}");
        }

        out.flush();
    }

    private String extractJsonValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\":\"");
        if (keyIndex == -1) return "";
        int startIndex = keyIndex + key.length() + 4;
        int endIndex = json.indexOf("\"", startIndex);
        return endIndex > startIndex ? json.substring(startIndex, endIndex) : "";
    }

    private boolean validateUserCredentials(String email, String password) {
        boolean isValid = false;

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT password_hash FROM login_info WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password_hash");
                    isValid = checkPassword(password, storedHashedPassword);
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
