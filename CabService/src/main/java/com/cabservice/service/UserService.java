package com.cabservice.service;

import com.cabservice.factory.UserFactory;
import com.cabservice.model.User;
import com.cabservice.util.DatabaseConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public boolean createUser(String role, String firstName, String lastName, String nic, String tp, String email, String password, String assignedVehicle, String accountStatus) {

        User user = UserFactory.createUser(role, firstName, lastName, nic, tp, email, password, assignedVehicle, accountStatus);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertUser = "INSERT INTO users (first_name, last_name, nic, tp, email, account_type, assigned_vehicle, account_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getFirstName());
                stmt.setString(2, user.getLastName());
                stmt.setString(3, user.getNic());
                stmt.setString(4, user.getTp());
                stmt.setString(5, user.getEmail());
                stmt.setString(6, user.getRole());
                stmt.setString(7, user.getAssignedVehicle());
                stmt.setString(8, "active");
                stmt.executeUpdate();

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    // If assignedVehicle is not "not assigned", update the vehicles table

                    if (!"not assigned".equals(assignedVehicle) && !"none".equals(assignedVehicle)) {
                        updateVehicleAssignedToUser(conn, assignedVehicle);
                    }

                    return insertLoginInfo(userId, user.getEmail(), user.getPassword());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void updateVehicleAssignedToUser(Connection conn, String assignedVehicle) {
        String updateVehicleQuery = "UPDATE vehicles SET status = 'assigned' WHERE number_plate = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateVehicleQuery)) {
            stmt.setString(1, assignedVehicle);  // This is the assigned vehicle's number plate
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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


    public List<User> getAllDrivers() {
        List<User> drivers = new ArrayList<>();
        String query = "SELECT * FROM users WHERE account_type = 'driver'";

        // Establish the connection and fetch data
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Iterate over the result set and create Driver objects using the factory method
            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String nic = resultSet.getString("nic");
                String tp = resultSet.getString("tp");
                String email = resultSet.getString("email");

                String password = getPasswordFromLoginInfo(email);

                String assignedVehicle = resultSet.getString("assigned_vehicle");
                String accountStatus = resultSet.getString("account_status");

                // Use the UserFactory to create the appropriate user type (driver)
                User driver = UserFactory.createUser("driver", firstName, lastName, nic, tp, email, password, assignedVehicle, accountStatus );
                drivers.add(driver);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return drivers;
    }

    private String getPasswordFromLoginInfo(String email) {
        String password = null;
        String query = "SELECT password_hash FROM login_info WHERE email = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                password = resultSet.getString("password_hash");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return password;
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
    public boolean deleteDriverByEmail(String email) {
        String getVehicleSql = "SELECT assigned_vehicle FROM users WHERE email = ?";
        String updateVehicleSql = "UPDATE vehicles SET status = 'not assigned' WHERE number_plate = ?";
        String deleteUserSql = "DELETE FROM users WHERE email = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement getVehicleStmt = conn.prepareStatement(getVehicleSql)) {
                getVehicleStmt.setString(1, email);
                ResultSet rs = getVehicleStmt.executeQuery();
                String assignedVehicle = null;

                if (rs.next()) {
                    assignedVehicle = rs.getString("assigned_vehicle");
                }

                // Step 2: If a vehicle is assigned, update its status
                if (assignedVehicle != null && !assignedVehicle.equalsIgnoreCase("not assigned")) {
                    try (PreparedStatement updateVehicleStmt = conn.prepareStatement(updateVehicleSql)) {
                        updateVehicleStmt.setString(1, assignedVehicle);
                        updateVehicleStmt.executeUpdate();
                    }
                }
            }

            // Step 3: Delete the driver
            try (PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSql)) {
                deleteUserStmt.setString(1, email);
                int rowsAffected = deleteUserStmt.executeUpdate();

                conn.commit(); // Commit the transaction
                return rowsAffected > 0; // Return true if the driver was deleted
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on failure
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false; // Return false if any error occurs
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit mode
                    conn.close(); // Close connection
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }


}
