package com.cabservice.model;

public class Route {
    private int id;
    private String locationA;
    private String locationB;
    private double distance;

    public Route(int id, String locationA, String locationB, double distance) {
        this.id = id;
        this.locationA = locationA;
        this.locationB = locationB;
        this.distance = distance;
    }

    public Route(String locationA, String locationB, double distance) {
        this.locationA = locationA;
        this.locationB = locationB;
        this.distance = distance;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationA() {
        return locationA;
    }

    public void setLocationA(String locationA) {
        this.locationA = locationA;
    }

    public String getLocationB() {
        return locationB;
    }

    public void setLocationB(String locationB) {
        this.locationB = locationB;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}