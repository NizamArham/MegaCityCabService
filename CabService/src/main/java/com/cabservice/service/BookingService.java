package com.cabservice.service;

import com.cabservice.util.DatabaseConnection;

import java.sql.*;

public class BookingService {

    public boolean saveBooking(String pickUpAddress, String dropAddress, String cabClass, String vehicleType) {
        String sql = "INSERT INTO bookings (pickUpAddress, dropAddress, cabClass, vehicleType) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pickUpAddress);
            stmt.setString(2, dropAddress);
            stmt.setString(3, cabClass);
            stmt.setString(4, vehicleType);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
