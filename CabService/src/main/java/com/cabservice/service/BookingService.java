package com.cabservice.service;

import com.cabservice.strategy.FareCalculator;
import com.cabservice.strategy.FareStrategy;
import com.cabservice.strategy.FareStrategyFactory;
import com.cabservice.util.DatabaseConnection;

import java.sql.*;

public class BookingService {

<<<<<<< HEAD
    public int saveBooking(String pickUpAddress, String dropAddress, String cabClass, String vehicleType, String bookingStatus, String passengerEmail) {
        int bookingId = 0;
        String sql = "INSERT INTO bookings (pickUpAddress, dropAddress, cabClass, vehicleType, bookingStatus, passengerEmail) VALUES (?, ?, ?, ?, ?, ?)";
=======
    public boolean saveBooking(String pickUpAddress, String dropAddress, String cabClass, String vehicleType, String bookingStatus, String passengerEmail) {

        double distance = calculateDistance(pickUpAddress, dropAddress);

        if (distance == -1) {
            System.out.println("Route not found in database!");
            return false;
        }

        FareStrategy strategy = FareStrategyFactory.getFareStrategy(vehicleType);
        FareCalculator fareCalculator = new FareCalculator(strategy);

        // Calculate fare
        double fare = fareCalculator.calculateFare(distance, cabClass);

        String sql = "INSERT INTO bookings (pickUpAddress, dropAddress, cabClass, vehicleType, bookingStatus, passengerEmail, fare) VALUES (?, ?, ?, ?, ?, ?, ?)";
>>>>>>> development

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, pickUpAddress);
            stmt.setString(2, dropAddress);
            stmt.setString(3, cabClass);
            stmt.setString(4, vehicleType);
            stmt.setString(5, bookingStatus);
            stmt.setString(6, passengerEmail);
<<<<<<< HEAD
=======
            stmt.setDouble(7, fare); // Store calculated fare

            return stmt.executeUpdate() > 0;
>>>>>>> development

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        bookingId = generatedKeys.getInt(1); // Retrieve the auto-generated booking ID
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
}
