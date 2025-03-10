package com.cabservice.service;

import com.cabservice.model.Passenger;
import com.cabservice.model.Ride;
import com.cabservice.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RideService {

    public List<Ride> getRidesForLocation(String location, String driverEmail) {
        List<Ride> rides = new ArrayList<>();

        // Step 1: Get assigned vehicle for the driver
        String assignedVehicle = getAssignedVehicle(driverEmail);
        if (assignedVehicle == null) {
            System.out.println("No assigned vehicle found for driver: " + driverEmail);
            return rides;
        }

        // Step 2: Get vehicle type and cab class
        String[] vehicleDetails = getVehicleDetails(assignedVehicle);
        if (vehicleDetails == null) {
            System.out.println("No vehicle details found for vehicle: " + assignedVehicle);
            return rides;
        }

        String vehicleType = vehicleDetails[0];
        String cabClass = vehicleDetails[1];

        // Step 3: Fetch rides that match criteria
        String rideQuery = "SELECT id, passengerEmail, pickUpAddress, dropAddress FROM bookings " +
                "WHERE pickUpAddress = ? AND cabClass = ? AND vehicleType = ? AND bookingStatus = 'Requested'";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(rideQuery)) {

            statement.setString(1, location);
            statement.setString(2, cabClass);
            statement.setString(3, vehicleType);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String passengerEmail = resultSet.getString("passengerEmail");

                // Fetch passenger details
                Passenger passenger = getPassengerDetails(passengerEmail);

                if (passenger != null) {
                    Ride ride = new Ride(
                            resultSet.getInt("id"),
                            assignedVehicle,
                            passenger.getFirstName(),
                            resultSet.getString("pickUpAddress"),
                            resultSet.getString("dropAddress"),
                            passenger.getTp(),
                            passenger.getEmail()
                    );
                    rides.add(ride);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rides;
    }

    private String getAssignedVehicle(String driverEmail) {
        String query = "SELECT assigned_Vehicle FROM users WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, driverEmail);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("assigned_Vehicle");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no vehicle assigned
    }

    private String[] getVehicleDetails(String assignedVehicle) {
        String query = "SELECT vehicle_type, cabClass FROM vehicles WHERE number_plate = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, assignedVehicle);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new String[]{resultSet.getString("vehicle_type"), resultSet.getString("cabClass")};
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if vehicle details not found
    }

    private Passenger getPassengerDetails(String email) {
        String userQuery = "SELECT first_name, tp, email FROM users WHERE email = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(userQuery)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Passenger(
                        resultSet.getString("first_name"),
                        resultSet.getString("tp"),
                        email
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Return null if user not found
    }
}
