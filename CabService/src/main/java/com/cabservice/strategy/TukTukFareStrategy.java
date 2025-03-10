package com.cabservice.strategy;

// TukTuk Fare Strategy
public class TukTukFareStrategy implements FareStrategy {
    private static final double BASIC_RATE = 100;
    private static final double PER_KM_RATE = 100;

    @Override
    public double calculateBaseFare(double distance) {
        return BASIC_RATE + (PER_KM_RATE * distance);
    }
}