package com.cabservice.model;

public class Ride {
    private int rideId;
    private double fare;
    private String vehicle;
    private String passengerName;
    private String driver;
    private String driverLocation;
    private String pickupLocation;
    private String destination;
    private String passengerContact;
    private String passengerEmail;


    public Ride(int rideId, double fare, String vehicle, String passengerName, String driver, String driverLocation, String pickupLocation, String destination, String passengerContact, String passengerEmail) {
        this.rideId = rideId;
        this.fare = fare;
        this.vehicle = vehicle;
        this.passengerName = passengerName;
        this.driver = driver;
        this.driverLocation = driverLocation;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.passengerContact = passengerContact;
        this.passengerEmail = passengerEmail;
    }

    public Ride(int rideId, String vehicle, String passengerName, String pickupLocation, String destination, String passengerContact, String passengerEmail) {
        this.rideId = rideId;
        this.vehicle = vehicle;
        this.passengerName = passengerName;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.passengerContact = passengerContact;
        this.passengerEmail = passengerEmail;
    }

    public int getRideId() {
        return rideId;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(String driverLocation) {
        this.driverLocation = driverLocation;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPassengerContact() {
        return passengerContact;
    }

    public void setPassengerContact(String passengerContact) {
        this.passengerContact = passengerContact;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }
}
