package com.cabservice.service;

import com.cabservice.strategy.FareCalculator;
import com.cabservice.strategy.FareStrategy;
import com.cabservice.strategy.FareStrategyFactory;
import com.cabservice.util.DatabaseConnection;

import java.sql.*;

public class BookingService {

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

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pickUpAddress);
            stmt.setString(2, dropAddress);
            stmt.setString(3, cabClass);
            stmt.setString(4, vehicleType);
            stmt.setString(5, bookingStatus);
            stmt.setString(6, passengerEmail);
            stmt.setDouble(7, fare); // Store calculated fare

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
}
