package com.cabservice.model;

// Passenger Class
public class Passenger extends User {
    public Passenger(String firstName, String lastName, String nic, String tp, String email, String password, String assignedVehicle, String accountStatus) {
        super(firstName, lastName, nic, tp, email, password, assignedVehicle, accountStatus);
    }

    public Passenger( String firstName, String tp, String email) {
        super( firstName, tp, email);
    }

    @Override
    public String getRole() {
        return "passenger";
    }
}
