package com.cabservice.service;

import com.cabservice.model.Route;
import com.cabservice.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class RouteService {

    // Adds a new route to the database
    public boolean addRoute(Route route) {
        String query = "INSERT INTO routes (locationA, locationB, distance) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, route.getLocationA());
            statement.setString(2, route.getLocationB());
            statement.setDouble(3, route.getDistance());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Retrieves all routes from the database
    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        String query = "SELECT * FROM routes";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                routes.add(new Route(
                        resultSet.getInt("id"),
                        resultSet.getString("locationA"),
                        resultSet.getString("locationB"),
                        resultSet.getDouble("distance")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return routes;
    }

    // Retrieves unique locations that match the given query prefix (case-insensitive)
    public Set<String> getMatchingLocations(String query) {
        Set<String> locations = new HashSet<>();
        String sql = "SELECT locationA, locationB FROM routes";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String locationA = rs.getString("locationA");
                String locationB = rs.getString("locationB");

                if (locationA != null && locationA.toLowerCase().startsWith(query.toLowerCase())) {
                    locations.add(locationA);
                }
                if (locationB != null && locationB.toLowerCase().startsWith(query.toLowerCase())) {
                    locations.add(locationB);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return locations;
    }

    // Retrieves all unique locations from the database, sorted alphabetically
    public Set<String> getAllLocations() {
        Set<String> locations = new TreeSet<>(); // TreeSet ensures uniqueness + sorting
        String sql = "SELECT locationA, locationB FROM routes";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Collections.addAll(locations, rs.getString("locationA"), rs.getString("locationB"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return locations;
    }
}
