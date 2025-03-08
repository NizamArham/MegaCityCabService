package com.cabservice.strategy;


public class CarFareStrategy implements FareStrategy {
    private static final double BASIC_RATE = 200;
    private static final double PER_KM_RATE = 150;

    @Override
    public double calculateBaseFare(double distance) {
        return BASIC_RATE + (PER_KM_RATE * distance);
    }
}