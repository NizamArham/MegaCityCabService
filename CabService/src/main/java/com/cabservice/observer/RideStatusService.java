package com.cabservice.observer;

import java.util.HashMap;
import java.util.Map;

public class RideStatusService {
    private Map<String, RideStatus> rideStatuses = new HashMap<>();

    // Ensure each booking has its own RideStatus instance
    private RideStatus getRideStatusInstance(String bookingId) {
        return rideStatuses.computeIfAbsent(bookingId, k -> new RideStatus());
    }

    // Passenger makes a ride request
    public void passengerRequestsRide(String bookingId) {
        RideStatus rideStatus = getRideStatusInstance(bookingId);
        rideStatus.setStatus("requested");
        System.out.println("Passenger made a booking with ID: " + bookingId);
    }

    // Driver accepts the ride
    public void driverAcceptsRide(String bookingId) {
        RideStatus rideStatus = getRideStatusInstance(bookingId);
        rideStatus.setStatus("accepted");
        System.out.println("Driver accepted the ride for booking ID: " + bookingId);
    }

    // Add a passenger observer for a specific booking
    public void addPassengerObserver(String bookingId, Observer observer) {
        RideStatus rideStatus = getRideStatusInstance(bookingId);
        rideStatus.addObserver(observer);
    }

    // Remove a passenger observer for a specific booking
    public void removePassengerObserver(String bookingId, Observer observer) {
        RideStatus rideStatus = getRideStatusInstance(bookingId);
        rideStatus.removeObserver(observer);
    }

    // Get the latest status of a ride
    public String getRideStatus(String bookingId) {
        RideStatus rideStatus = rideStatuses.get(bookingId);
        return (rideStatus != null) ? rideStatus.getStatus() : "unknown";
    }
}
