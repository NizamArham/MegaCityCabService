package com.cabservice.model;

public class StatusResponse {

    private String rideStatus;

    public StatusResponse(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }
}
