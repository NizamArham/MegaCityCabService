package com.cabservice.model;

// Admin Class
public class Admin extends User {
    public Admin(String firstName, String lastName, String nic, String tp, String email, String password, String assignedVehicle, String accountStatus) {
        super(firstName, lastName, nic, tp, email, password, assignedVehicle , accountStatus);

    }

    @Override
    public String getRole() {
        return "admin";
    }
}
