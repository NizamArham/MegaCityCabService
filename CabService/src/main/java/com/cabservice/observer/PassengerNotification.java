package com.cabservice.observer;

import java.util.ArrayList;
import java.util.List;

public class PassengerNotification implements Observer {

    private String bookingId;
    private List<String> updates; // Store messages instead of writing to response

    public PassengerNotification(String bookingId) {
        this.bookingId = bookingId;
        this.updates = new ArrayList<>();
    }

    @Override
    public void update(String message) {
        System.out.println("Ride status for booking " + bookingId + ": " + message);
        updates.add(message); // Store updates
    }

    public List<String> getUpdates() {
        return updates; // Fetch updates when needed
    }
}
