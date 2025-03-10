package com.cabservice.service;

import com.cabservice.model.Passenger;
import com.cabservice.model.Ride;
import com.cabservice.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

        return null;
    }

    public static Optional<Ride> getOngoingRideByEmailForUser(String userEmail) {


        // SQL query to fetch ride details based on the passenger email and status = 'occupied'
        String query = "SELECT id, pickUpAddress, dropAddress,cabClass,vehicleType,bookingStatus,createdTime,updatedTime,driver_email,cab_number_plate,fare FROM bookings WHERE passengerEmail = ? AND bookingStatus = 'occupied'";
        String driverQuery = "SELECT first_name, tp FROM users WHERE email = ?";
        String distanceQuery = "SELECT distance FROM routes WHERE "
                + "(locationA = ? AND locationB = ?) OR "
                + "(locationA = ? AND locationB = ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {


            // Set the email parameter for the booking query
            statement.setString(1, userEmail);

            // Execute the query to get the ride details
            ResultSet resultSet = statement.executeQuery();

            double estimatedFare = 0.0;

            if (resultSet.next()) {
                // Retrieve ride details
                int bookingId = resultSet.getInt("id");
                String pickupLocation = resultSet.getString("pickUpAddress");
                String destinationName = resultSet.getString("dropAddress");
                String vehicleClass = resultSet.getString("cabClass");
                String vehicleType = resultSet.getString("vehicleType");
                String bookingStatus = resultSet.getString("bookingStatus");
                String bookedTime = resultSet.getString("createdTime");
                String updatedTime = resultSet.getString("updatedTime");
                String driverEmail = resultSet.getString("driver_email");
                String vehicleNo = resultSet.getString("cab_number_plate");
                estimatedFare = Double.parseDouble(resultSet.getString("fare"));

                // Fetch the driver details from the users table
                try (PreparedStatement driverStatement = connection.prepareStatement(driverQuery)) {
                    driverStatement.setString(1, driverEmail);
                    ResultSet driverResultSet = driverStatement.executeQuery();

                    String driverName = null;
                    String driverTel = null;

                    if (driverResultSet.next()) {
                        driverName = driverResultSet.getString("first_name");
                        driverTel = driverResultSet.getString("tp");

                        // Fetch the distance between pickup and destination
                        try (PreparedStatement distanceStatement = connection.prepareStatement(distanceQuery)) {
                            distanceStatement.setString(1, pickupLocation);
                            distanceStatement.setString(2, destinationName);
                            distanceStatement.setString(3, destinationName);
                            distanceStatement.setString(4, pickupLocation);

                            ResultSet distanceResultSet = distanceStatement.executeQuery();

                            String distance = null;

                            if (distanceResultSet.next()) {
                                distance = distanceResultSet.getString("distance");
                            }


                            // Create the Ride object with all the ride and driver details
                            Ride ride = new Ride(
                                    bookingId,
                                    pickupLocation,
                                    destinationName,
                                    bookedTime,
                                    updatedTime,
                                    vehicleType,
                                    vehicleClass,
                                    vehicleNo,
                                    driverName,
                                    driverTel,
                                    distance,
                                    estimatedFare,
                                    bookingStatus
                            );

                            return Optional.of(ride);

                        } catch (SQLException e) {
                            // Handle any SQL exceptions related to fetching the distance
                            throw new RuntimeException("Error fetching distance data", e);
                        }
                    } else {
                        // Handle case where no driver data is found
                        throw new RuntimeException("Driver not found for the provided email.");
                    }
                } catch (SQLException e) {
                    // Handle any SQL exceptions related to fetching driver details
                    throw new RuntimeException("Error fetching driver details", e);
                }
            }
        } catch (SQLException e) {
            // Handle general SQL exceptions
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.empty();  // Return empty if no ride found
    }


    public static List<Ride> getPastRidesByEmailForUser(String userEmail) {
        System.out.println("Fetching past rides for: " + userEmail);

        // SQL query to fetch past ride details based on the passenger email and booking status (completed or canceled)
        String query = "SELECT id, pickUpAddress, dropAddress, cabClass, vehicleType, bookingStatus, createdTime, updatedTime, driver_email, cab_number_plate, fare , payment_status, payment_method FROM bookings WHERE passengerEmail = ? AND (bookingStatus = 'completed' OR bookingStatus = 'canceled')";
        String driverQuery = "SELECT first_name, tp FROM users WHERE email = ?";
        String distanceQuery = "SELECT distance FROM routes WHERE "
                + "(locationA = ? AND locationB = ?) OR "
                + "(locationA = ? AND locationB = ?)";

        List<Ride> pastRides = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set the email parameter for the booking query
            statement.setString(1, userEmail);

            // Execute the query to get the past ride details
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // Retrieve ride details
                int bookingId = resultSet.getInt("id");
                String pickupLocation = resultSet.getString("pickUpAddress");
                String destinationName = resultSet.getString("dropAddress");
                String vehicleClass = resultSet.getString("cabClass");
                String vehicleType = resultSet.getString("vehicleType");
                String bookingStatus = resultSet.getString("bookingStatus");
                String bookedTime = resultSet.getString("createdTime");
                String updatedTime = resultSet.getString("updatedTime");
                String driverEmail = resultSet.getString("driver_email");
                String vehicleNo = resultSet.getString("cab_number_plate");
                double estimatedFare = Double.parseDouble(resultSet.getString("fare"));
                String paymentStatus = resultSet.getString("payment_status");
                String paymentMethod = resultSet.getString("payment_method");

                // Fetch the driver details from the users table
                try (PreparedStatement driverStatement = connection.prepareStatement(driverQuery)) {
                    driverStatement.setString(1, driverEmail);
                    ResultSet driverResultSet = driverStatement.executeQuery();

                    String driverName = null;
                    String driverTel = null;

                    if (driverResultSet.next()) {
                        driverName = driverResultSet.getString("first_name");
                        driverTel = driverResultSet.getString("tp");

                        // Fetch the distance between pickup and destination
                        try (PreparedStatement distanceStatement = connection.prepareStatement(distanceQuery)) {
                            distanceStatement.setString(1, pickupLocation);
                            distanceStatement.setString(2, destinationName);
                            distanceStatement.setString(3, destinationName);
                            distanceStatement.setString(4, pickupLocation);

                            ResultSet distanceResultSet = distanceStatement.executeQuery();

                            String distance = null;

                            if (distanceResultSet.next()) {
                                distance = distanceResultSet.getString("distance");
                            }

                            System.out.println("Fetching past ride for: " + userEmail + " - Ride ID: " + bookingId);

                            Ride ride = new Ride(
                                    bookingId,
                                    pickupLocation,
                                    destinationName,
                                    bookedTime,
                                    updatedTime,
                                    vehicleType,
                                    vehicleClass,
                                    vehicleNo,
                                    driverName,
                                    driverTel,
                                    distance,
                                    estimatedFare,
                                    bookingStatus,
                                    paymentStatus,
                                    paymentMethod
                            );

                            System.out.println("Fetching past ride for: " + userEmail + " payment: " + paymentMethod);

                            pastRides.add(ride);

                        } catch (SQLException e) {
                            // Handle any SQL exceptions related to fetching the distance
                            throw new RuntimeException("Error fetching distance data", e);
                        }
                    } else {
                        // Handle case where no driver data is found
                        throw new RuntimeException("Driver not found for the provided email.");
                    }
                } catch (SQLException e) {
                    // Handle any SQL exceptions related to fetching driver details
                    throw new RuntimeException("Error fetching driver details", e);
                }
            }

        } catch (SQLException e) {
            // Handle general SQL exceptions
            e.printStackTrace();
            return Collections.emptyList();
        }

        return pastRides;
    }


        // Make this method static
        public static String getRideStatus(String bookingId) {
            String status = "Requested";  // Default status

            // SQL query to fetch booking status
            String query = "SELECT bookingStatus FROM bookings WHERE id = ?";

            // Connection object (you should use your own database connection setup)
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                // Set the bookingId in the query
                stmt.setString(1, bookingId);

                // Execute the query
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        status = rs.getString("bookingStatus");  // Get the bookingStatus from the result set
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                // Optionally, log the error and return a suitable error message
            }

            return status;  // Return the status, e.g., "Requested", "Occupied", etc.
        }

}

