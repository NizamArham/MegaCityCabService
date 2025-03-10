package com.cabservice.model;

public class Ride {
    private int rideId;
    private double fare;
    private String vehicle;
    private String vehicleClass;
    private String vehicleType;
    private String bookingStatus;
    private String bookedTime;
    private String updatedTime;
    private String paymentStatus;
    private String paymentMethod;
    private String passengerName;
    private String driverName;
    private String driverTel;
    private String driverLocation;
    private String pickupLocation;
    private String destination;
    private String passengerContact;
    private String passengerEmail;
    private String distance;

    public Ride(int rideId, String pickupLocation, String destination, String bookedTime, String updatedTime, String vehicleType, String vehicleClass, String vehicle, String driverName, String driverTel, String distance, double fare, String bookingStatus) {
        this.rideId = rideId;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.bookedTime = bookedTime;
        this.updatedTime = updatedTime;
        this.vehicleType = vehicleType;
        this.vehicleClass = vehicleClass;
        this.vehicle = vehicle;
        this.driverName = driverName;
        this.driverTel = driverTel;
        this.distance = distance;
        this.fare = fare;
        this.bookingStatus = bookingStatus;
    }

    public Ride(int rideId, String pickupLocation, String destination, String bookedTime, String updatedTime, String vehicleType, String vehicleClass, String vehicle, String driverName, String driverTel, String distance, double fare, String bookingStatus , String paymentStatus ,  String paymentMethod) {
        this.rideId = rideId;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.bookedTime = bookedTime;
        this.updatedTime = updatedTime;
        this.vehicleType = vehicleType;
        this.vehicleClass = vehicleClass;
        this.vehicle = vehicle;
        this.driverName = driverName;
        this.driverTel = driverTel;
        this.distance = distance;
        this.fare = fare;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;


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


    public String getDriverTel() {
        return driverTel;
    }

    public void setDriverTel(String driverTel) {
        this.driverTel = driverTel;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getBookedTime() {
        return bookedTime;
    }

    public void setBookedTime(String bookedTime) {
        this.bookedTime = bookedTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
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
