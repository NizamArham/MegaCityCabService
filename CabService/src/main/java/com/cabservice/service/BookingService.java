package com.cabservice.service;

import com.cabservice.util.DatabaseConnection;

import java.sql.*;

public class BookingService {

    public int saveBooking(String pickUpAddress, String dropAddress, String cabClass, String vehicleType, String bookingStatus, String passengerEmail) {
        int bookingId = 0;
        String sql = "INSERT INTO bookings (pickUpAddress, dropAddress, cabClass, vehicleType, bookingStatus, passengerEmail) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, pickUpAddress);
            stmt.setString(2, dropAddress);
            stmt.setString(3, cabClass);
            stmt.setString(4, vehicleType);
            stmt.setString(5, bookingStatus);
            stmt.setString(6, passengerEmail);

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
}
