package com.cabservice.observer;

import java.util.ArrayList;
import java.util.List;

public class RideStatus {
    private List<Observer> observers = new ArrayList<>();
    private String status;

    // Add observer
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    // Remove observer
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    // Notify all observers about the change in status
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(status);
        }
    }

    // Set the ride status and notify observers
    public void setStatus(String status) {
        this.status = status;
        notifyObservers();
    }

    // Get the current ride status
    public String getStatus() {
        return status;
    }

}
