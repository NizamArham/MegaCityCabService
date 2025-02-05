package com.cabservice.service;

import com.cabservice.model.Vehicle;
import com.cabservice.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleService {

    public boolean saveVehicle(Vehicle vehicle) {
        String query = "INSERT INTO vehicles (brand, model, engine_capacity, color, number_plate, seat_capacity, vehicle_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, vehicle.getBrand());
            stmt.setString(2, vehicle.getModel());
            stmt.setString(3, vehicle.getEngineCapacity());
            stmt.setString(4, vehicle.getColor());
            stmt.setString(5, vehicle.getNumberPlate());
            stmt.setString(6, vehicle.getSeatCapacity());
            stmt.setString(7, vehicle.getVehicleType());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isVehicleExists(String numberPlate) {
        String query = "SELECT 1 FROM vehicles WHERE number_plate = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, numberPlate);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if a matching record is found
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT * FROM vehicles";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setId(rs.getInt("id"));
                vehicle.setBrand(rs.getString("brand"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setEngineCapacity(rs.getString("engine_capacity"));
                vehicle.setColor(rs.getString("color"));
                vehicle.setNumberPlate(rs.getString("number_plate"));
                vehicle.setSeatCapacity(rs.getString("seat_capacity"));
                vehicle.setVehicleType(rs.getString("vehicle_type"));

                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    public static String convertListToJson(List<Vehicle> vehicles) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle v = vehicles.get(i);
            json.append("{")
                    .append("\"id\":").append(v.getId()).append(",")
                    .append("\"brand\":\"").append(v.getBrand()).append("\",")
                    .append("\"model\":\"").append(v.getModel()).append("\",")
                    .append("\"engineCapacity\":\"").append(v.getEngineCapacity()).append("\",")
                    .append("\"color\":\"").append(v.getColor()).append("\",")
                    .append("\"numberPlate\":\"").append(v.getNumberPlate()).append("\",")
                    .append("\"seatCapacity\":").append(v.getSeatCapacity()).append(",")
                    .append("\"vehicleType\":\"").append(v.getVehicleType()).append("\"")
                    .append("}");
            if (i < vehicles.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
}
