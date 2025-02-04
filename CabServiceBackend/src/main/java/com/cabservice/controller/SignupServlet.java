package com.cabservice.controller;

import com.cabservice.util.DatabaseConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        PrintWriter out = response.getWriter();

        // Read JSON from request body
        StringBuilder jsonString = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
        }

        // Convert JSON string to key-value pairs
        Map<String, String> jsonData = parseJson(jsonString.toString());

        String firstName = jsonData.get("firstName");
        String lastName = jsonData.get("lastName");
        String nic = jsonData.get("nic");
        String tp = jsonData.get("tp");
        String email = jsonData.get("email");
        String password = jsonData.get("password");

        if (firstName == null || lastName == null || nic == null || tp == null || email == null || password == null) {
            out.write("{\"status\":\"error\", \"message\":\"All fields are required\"}");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if email, NIC, or TP exists
            String checkQuery = "SELECT email, nic, tp FROM users WHERE email = ? OR nic = ? OR tp = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, email);
                checkStmt.setString(2, nic);
                checkStmt.setString(3, tp);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    String message = "";
                    if (rs.getString("email").equals(email)) {
                        message = "This email is already in use! Try a new one to get started";
                    } else if (rs.getString("nic").equals(nic)) {
                        message = "This NIC is already in use! Try a new one to get started";
                    } else if (rs.getString("tp").equals(tp)) {
                        message = "This Phone Number is already in use! Try a new one to get started";
                    }
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    out.write("{\"status\":\"error\", \"message\":\"" + message + "\"}");
                    out.flush();
                    return;
                }
            }

            // Insert user data into users table
            String insertUser = "INSERT INTO users (first_name, last_name, nic, tp, email, account_type, account_status) VALUES (?, ?, ?, ?, ?, 'customer', 'active')";
            int userId;
            try (PreparedStatement userStmt = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, firstName);
                userStmt.setString(2, lastName);
                userStmt.setString(3, nic);
                userStmt.setString(4, tp);
                userStmt.setString(5, email);
                userStmt.executeUpdate();

                ResultSet generatedKeys = userStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                } else {
                    out.write("{\"status\":\"error\", \"message\":\"Failed to create account\"}");
                    return;
                }
            }

            // Insert login data into login_info table
            String hashedPassword = hashPassword(password);
            String insertLogin = "INSERT INTO login_info (user_id, email, password_hash) VALUES (?, ?, ?)";
            try (PreparedStatement loginStmt = conn.prepareStatement(insertLogin)) {
                loginStmt.setInt(1, userId);
                loginStmt.setString(2, email);
                loginStmt.setString(3, hashedPassword);
                loginStmt.executeUpdate();
            }

            out.write("{\"status\":\"success\", \"message\":\"Signup successful\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            out.write("{\"status\":\"error\", \"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private Map<String, String> parseJson(String json) {
        Map<String, String> data = new HashMap<>();
        json = json.replaceAll("[{}\"]", "");
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] entry = pair.split(":");
            if (entry.length == 2) {
                data.put(entry[0].trim(), entry[1].trim());
            }
        }
        return data;
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
