package com.cabservice.model;

public abstract class Vehicle {

    private int id;
    private String brand;
    private String model;
    private String fuelType;
    private String color;
    private String numberPlate;
    private int seatCapacity;
    private String cabClass;
    private String vehicleType;
    private int powerSourceCapacity;
    private String status;

    // Constructor
    public Vehicle(String brand, String model, String fuelType, int powerSourceCapacity,
                   String color, String numberPlate, int seatCapacity, String cabClass, String vehicleType) {
        this.brand = brand;
        this.model = model;
        this.fuelType = fuelType;
        this.powerSourceCapacity = powerSourceCapacity;
        this.color = color;
        this.numberPlate = numberPlate;
        this.seatCapacity = seatCapacity;
        this.cabClass = cabClass;
        this.vehicleType = vehicleType;
        this.status = "not assigned";
    }

    public Vehicle() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public int getSeatCapacity() {
        return seatCapacity;
    }

    public void setSeatCapacity(int seatCapacity) {
        this.seatCapacity = seatCapacity;
    }

    public String getCabClass() {
        return cabClass;
    }

    public void setCabClass(String cabClass) {
        this.cabClass = cabClass;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getPowerSourceCapacity() {
        return powerSourceCapacity;
    }

    public void setPowerSourceCapacity(int powerSourceCapacity) {
        this.powerSourceCapacity = powerSourceCapacity;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public abstract double calculateFare(double distance);


}