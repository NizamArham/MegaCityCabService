package com.cabservice.observer;

public class RideStatusService {
    private RideStatus rideStatus;

    public RideStatusService() {
        this.rideStatus = new RideStatus();
    }

    // Method to handle passenger making a ride request
    public void passengerRequestsRide(String bookingId) {
        // Set the ride status to 'requested' and notify observers
        rideStatus.setStatus("requested");
        System.out.println("Passenger made a booking with ID: " + bookingId);
    }

    // Method to handle driver accepting the ride
    public void driverAcceptsRide(String bookingId) {
        // Set the ride status to 'accepted' and notify observers
        rideStatus.setStatus("accepted");
        System.out.println("Driver accepted the ride for booking ID: " + bookingId);
    }

    // Method to register passenger observers
    public void addPassengerObserver(Observer observer) {
        rideStatus.addObserver(observer);
    }

    // Method to remove passenger observers
    public void removePassengerObserver(Observer observer) {
        rideStatus.removeObserver(observer);
    }
}
