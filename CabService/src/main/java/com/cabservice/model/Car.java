package com.cabservice.model;

public class Car extends Vehicle {
    public Car(String brand, String model, String fuelType, int powerSourceCapacity, String color,
               String numberPlate, int seatCapacity, String cabClass) {
        super(brand, model, fuelType, powerSourceCapacity, color, numberPlate, seatCapacity, cabClass, "Car");
    }

    @Override
    public double calculateFare(double distance) {
        return distance * 10;
    }
}
