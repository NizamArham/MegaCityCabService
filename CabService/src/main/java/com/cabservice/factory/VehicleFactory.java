package com.cabservice.factory;

import com.cabservice.model.Bike;
import com.cabservice.model.Bus;
import com.cabservice.model.Car;
import com.cabservice.model.Vehicle;

public class VehicleFactory {
    public static Vehicle createVehicle(String vehicleType, String brand, String model, String fuelType, int powersourceCapacity, String color, String numberPlate, int seatCapacity, String cabClass) {
        switch (vehicleType.toLowerCase()) {
            case "Van":
            case "Car":
                return new Car
                        (brand, model, fuelType, powersourceCapacity, color, numberPlate, seatCapacity, cabClass);
            case "Bike":
                return new Bike
                        (brand, model, fuelType, powersourceCapacity, color, numberPlate, seatCapacity, cabClass);
            case "Bus":
                return new Bus
                        (brand, model, fuelType, powersourceCapacity, color, numberPlate, seatCapacity, cabClass);
            default:
                throw new IllegalArgumentException("Invalid vehicle type: " + vehicleType);
        }
    }
}