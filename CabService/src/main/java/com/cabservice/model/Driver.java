package com.cabservice.model;

// Driver Class
public class Driver extends User {
    public Driver(String firstName, String lastName, String nic, String tp, String email, String password, String assignedVehicle, String accountStatus ) {
        super(firstName, lastName, nic, tp, email, password, assignedVehicle, accountStatus);
        // Additional driver-specific logic if needed
    }

    @Override
    public String getRole() {
        return "driver";
    }
}
