package com.cabservice.observer;

public class PassengerNotification implements Observer {

    private String bookingId;

    public PassengerNotification(String bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public void update(String message) {
        // Notify the passenger UI with the updated status
        System.out.println("Ride status for booking " + bookingId + ": " + message);
        // Here, you can trigger the front-end update via a polling mechanism.
    }
}
