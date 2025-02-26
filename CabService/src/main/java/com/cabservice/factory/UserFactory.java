package com.cabservice.factory;

import com.cabservice.model.Driver;
import com.cabservice.model.Passenger;
import com.cabservice.model.User;

public class UserFactory {

    public static User createUser(String role, String firstName, String lastName, String nic, String tp, String email, String password, String assignedVehicle, String accountStatus) {
        switch (role.toLowerCase()) {
            case "driver":
                return new Driver(firstName, lastName, nic, tp, email, password, assignedVehicle, accountStatus);
            case "passenger":
                return new Passenger(firstName, lastName, nic, tp, email, password, assignedVehicle,accountStatus );
            case "admin":
                return new Driver(firstName, lastName, nic, tp, email, password, assignedVehicle, accountStatus);
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}
