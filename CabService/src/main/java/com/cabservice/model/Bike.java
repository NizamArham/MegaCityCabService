package com.cabservice.model;

public class Bike extends Vehicle {
    public Bike(String brand, String model, String fuelType, int powerSourceCapacity, String color,
                String numberPlate, int seatCapacity, String cabClass) {
        super(brand, model, fuelType, powerSourceCapacity, color, numberPlate, seatCapacity, cabClass, "Bike");
    }

    @Override
    public double calculateFare(double distance) {
        return distance * 5; // Example: Bike fare is 5 per km
    }
}
