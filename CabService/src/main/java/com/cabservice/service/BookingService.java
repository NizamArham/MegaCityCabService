package com.cabservice.service;

import com.cabservice.strategy.FareCalculator;
import com.cabservice.strategy.FareStrategy;
import com.cabservice.strategy.FareStrategyFactory;
import com.cabservice.util.DatabaseConnection;

import java.sql.*;

public class BookingService {

    public int saveBooking(String pickUpAddress, String dropAddress, String cabClass, String vehicleType, String bookingStatus, String passengerEmail) {
        int bookingId = 0;

        // Step 1: Calculate Distance
        double distance = calculateDistance(pickUpAddress, dropAddress);
        if (distance < 0) {
            System.err.println("Error: No route found between " + pickUpAddress + " and " + dropAddress);
            return -1; // Indicate failure
        }

        // Step 2: Get Fare Strategy and Calculate Fare
        try {
            FareStrategy fareStrategy = FareStrategyFactory.getFareStrategy(vehicleType);
            FareCalculator fareCalculator = new FareCalculator(fareStrategy);
            double estimatedFare = fareCalculator.calculateFare(distance, cabClass);

            // Step 3: Insert Booking with Fare
            String sqlInsert = "INSERT INTO bookings (pickUpAddress, dropAddress, cabClass, vehicleType, bookingStatus, passengerEmail, fare) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, pickUpAddress);
                stmt.setString(2, dropAddress);
                stmt.setString(3, cabClass);
                stmt.setString(4, vehicleType);
                stmt.setString(5, bookingStatus);
                stmt.setString(6, passengerEmail);
                stmt.setDouble(7, estimatedFare); // Save calculated fare

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    // Step 4: Retrieve the bookingId using a SELECT query based on the same credentials
                    String sqlSelect = "SELECT id FROM bookings WHERE pickUpAddress = ? AND dropAddress = ? AND cabClass = ? AND vehicleType = ? AND bookingStatus = ? AND passengerEmail = ?";
                    try (PreparedStatement selectStmt = conn.prepareStatement(sqlSelect)) {
                        selectStmt.setString(1, pickUpAddress);
                        selectStmt.setString(2, dropAddress);
                        selectStmt.setString(3, cabClass);
                        selectStmt.setString(4, vehicleType);
                        selectStmt.setString(5, bookingStatus);
                        selectStmt.setString(6, passengerEmail);

                        try (ResultSet rs = selectStmt.executeQuery()) {
                            if (rs.next()) {
                                bookingId = rs.getInt("id");
                            }
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Invalid vehicle type: " + vehicleType);
            return -1;
        }

        return bookingId;
    }


    public static double calculateDistance(String pickUpAddress, String dropAddress) {
        String sql1 = "SELECT distance FROM routes WHERE locationA = ? AND locationB = ?";
        String sql2 = "SELECT distance FROM routes WHERE locationA = ? AND locationB = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(sql1);
             PreparedStatement stmt2 = conn.prepareStatement(sql2)) {

            // First check locationA -> locationB
            stmt1.setString(1, pickUpAddress);
            stmt1.setString(2, dropAddress);
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next()) {
                return rs1.getDouble("distance");
            }

            // If not found, check locationB -> locationA
            stmt2.setString(1, dropAddress);
            stmt2.setString(2, pickUpAddress);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                return rs2.getDouble("distance");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if no matching route is found
    }

    public boolean acceptRide(String bookingId, String driverEmail, String vehicle) {
        String checkVehicleStatusSql = "SELECT ride_status FROM vehicles WHERE number_plate = ?";
        String updateVehicleSql = "UPDATE vehicles SET ride_status = ? WHERE number_plate = ?";
        String updateBookingSql = "UPDATE bookings SET updatedTime = CURRENT_TIMESTAMP, driver_email = ?, cab_number_plate = ?, bookingStatus = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkVehicleStmt = conn.prepareStatement(checkVehicleStatusSql);
             PreparedStatement updateVehicleStmt = conn.prepareStatement(updateVehicleSql);
             PreparedStatement updateBookingStmt = conn.prepareStatement(updateBookingSql)) {

            // Check current ride status of the vehicle
            checkVehicleStmt.setString(1, vehicle);
            ResultSet rs = checkVehicleStmt.executeQuery();
            if (rs.next() && "occupied".equalsIgnoreCase(rs.getString("ride_status"))) {
                System.out.println("Driver is already in a ride!");
                return false;
            }

            // Update vehicle ride_status to "occupied"
            updateVehicleStmt.setString(1, "occupied");
            updateVehicleStmt.setString(2, vehicle);
            int vehicleUpdated = updateVehicleStmt.executeUpdate();

            // Update booking details
            updateBookingStmt.setString(1, driverEmail);
            updateBookingStmt.setString(2, vehicle);
            updateBookingStmt.setString(3, "occupied");
            updateBookingStmt.setInt(4, Integer.parseInt(bookingId)); // Convert bookingId to int

            int bookingUpdated = updateBookingStmt.executeUpdate();

            return vehicleUpdated > 0 && bookingUpdated > 0; // Return true only if both updates succeed

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

        public static String getBookingStatus(String bookingID) {
            String status = null;
            String query = "SELECT bookingStatus FROM bookings WHERE id = ?";  // SQL query

            // Use try-with-resources to ensure the connection, statement, and result set are closed automatically
            try (Connection connection = new DatabaseConnection().getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                // Set the bookingID parameter
                statement.setString(1, bookingID);

                // Execute the query and get the result
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Retrieve the booking status from the result set
                        status = resultSet.getString("bookingStatus");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();  // Log the exception for debugging purposes
            }

            // Return the status or "not found" if no status was found for the given bookingID
            return status != null ? status : "not found";
        }

}
