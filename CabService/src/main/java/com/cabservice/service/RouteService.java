package com.cabservice.service;

import com.cabservice.model.RouteModel;



import com.cabservice.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RouteService {
    public boolean addRoute(RouteModel route) {
        String query = "INSERT INTO routes (locationA, locationB, distance) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection(); // Use static method
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, route.getLocationA());
            statement.setString(2, route.getLocationB());
            statement.setDouble(3, route.getDistance());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Log error for debugging
            return false;
        }
    }

    public List<RouteModel> getAllRoutes() {
        List<RouteModel> routes = new ArrayList<>();
        String query = "SELECT * FROM routes";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String locationA = resultSet.getString("locationA");
                String locationB = resultSet.getString("locationB");
                double distance = resultSet.getDouble("distance");

                routes.add(new RouteModel(id, locationA, locationB, distance));
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log error
        }

        return routes;
    }

    public Set<String> getMatchingLocations(String query) {
        Set<String> locations = new HashSet<>();

        // Database connection and query execution
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT locationA, locationB FROM routes";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String locationA = rs.getString("locationA");
                    String locationB = rs.getString("locationB");

                    // Add locations to the set
                    if (locationA != null && locationA.toLowerCase().startsWith(query.toLowerCase())) {
                        locations.add(locationA);
                    }
                    if (locationB != null && locationB.toLowerCase().startsWith(query.toLowerCase())) {
                        locations.add(locationB);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return locations;
    }
}
