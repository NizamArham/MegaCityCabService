package com.cabservice.service;

import com.cabservice.model.Vehicle;
import com.cabservice.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VehicleService {

    // Method to save a vehicle in the database
    public boolean saveVehicle(String brand, String model, String fuelType, int powerCapacity,
                               String color, String numberPlate, int seatCapacity, String cabClass, String vehicleType) {

        if (isVehicleExists(numberPlate)) {
            return false; // Prevent duplicate vehicles
        }

        String query = "INSERT INTO vehicles (brand, model, fuel_type, engine_capacity, color, number_plate, seat_capacity, vehicle_type, cabClass) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, brand);
            stmt.setString(2, model);
            stmt.setString(3, fuelType);
            stmt.setInt(4, powerCapacity);
            stmt.setString(5, color);
            stmt.setString(6, numberPlate);
            stmt.setInt(7, seatCapacity);
            stmt.setString(8, vehicleType);
            stmt.setString(9, cabClass);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if vehicle exists based on number plate
    public boolean isVehicleExists(String numberPlate) {
        String query = "SELECT 1 FROM vehicles WHERE number_plate = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, numberPlate);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update the vehicle information
    public boolean updateVehicle(int id, String brand, String model, String fuelType, int powerCapacity,
                                 String color, String numberPlate, int seatCapacity, String cabClass, String vehicleType) {
        String query = "UPDATE vehicles SET brand = ?, model = ?, fuel_type = ?, engine_capacity = ?, color = ?, " +
                "number_plate = ?, seat_capacity = ?, cabClass = ?, vehicle_type = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, brand);
            stmt.setString(2, model);
            stmt.setString(3, fuelType);
            stmt.setInt(4, powerCapacity);
            stmt.setString(5, color);
            stmt.setString(6, numberPlate);
            stmt.setInt(7, seatCapacity);
            stmt.setString(8, cabClass);
            stmt.setString(9, vehicleType);
            stmt.setInt(10, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a vehicle from the database
    public boolean deleteVehicle(int vehicleId) {
        String query = "DELETE FROM vehicles WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, vehicleId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve all vehicles
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT * FROM vehicles";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Vehicle vehicle = new Vehicle() {
                    @Override
                    public double calculateFare(double distance) {
                        return 0;
                    }
                };

                vehicle.setId(rs.getInt("id"));
                vehicle.setBrand(rs.getString("brand"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setPowerSourceCapacity(rs.getInt("engine_capacity"));
                vehicle.setColor(rs.getString("color"));
                vehicle.setNumberPlate(rs.getString("number_plate"));
                vehicle.setSeatCapacity(rs.getInt("seat_capacity"));
                vehicle.setVehicleType(rs.getString("vehicle_type"));
                vehicle.setCabClass(rs.getString("cabClass"));
                vehicle.setFuelType(rs.getString("fuel_type"));
                vehicle.setStatus(rs.getString("driver_status"));

                vehicles.add(vehicle);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return vehicles;
    }

    // Validate the format of the vehicle's number plate
    public boolean isValidNumberPlate(String numberPlate) {
        String regex = "^[A-Za-z]{2,3}-\\d{4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(numberPlate);
        return matcher.matches();
    }

    // Get available vehicles
    public List<String> getAvailableVehicles() {
        List<String> availableVehicles = new ArrayList<>();
        for (Vehicle vehicle : getAllVehicles()) {
            if ("not assigned".equals(vehicle.getStatus())) {
                String vehicleJson = "{" +
                        "\"id\": " + vehicle.getId() + ", " +
                        "\"vehicleType\": \"" + vehicle.getVehicleType() + "\", " +
                        "\"licensePlate\": \"" + vehicle.getNumberPlate() + "\", " +
                        "\"brand\": \"" + vehicle.getBrand() + "\"}";
                availableVehicles.add(vehicleJson);
            }
        }
        return availableVehicles;
    }
}
