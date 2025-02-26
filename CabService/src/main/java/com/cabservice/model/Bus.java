package com.cabservice.model;

public class Bus extends Vehicle {
    public Bus(String brand, String model, String fuelType, int powerSourceCapacity, String color,
               String numberPlate, int seatCapacity, String cabClass) {
        super(brand, model, fuelType, powerSourceCapacity, color, numberPlate, seatCapacity, cabClass, "Bus");
    }

    @Override
    public double calculateFare(double distance) {
        return distance * 20; // Example: Bus fare is 20 per km
    }
}
