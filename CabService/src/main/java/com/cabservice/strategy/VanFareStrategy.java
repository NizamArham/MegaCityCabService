package com.cabservice.strategy;

public class VanFareStrategy implements FareStrategy {
    private static final double BASIC_RATE = 300;
    private static final double PER_KM_RATE = 200;

    @Override
    public double calculateBaseFare(double distance) {
        return BASIC_RATE + (PER_KM_RATE * distance);
    }
}
