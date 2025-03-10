package com.cabservice.strategy;

import com.cabservice.util.CabClassModifier;

public class FareCalculator {
    private FareStrategy fareStrategy;

    public FareCalculator(FareStrategy fareStrategy) {
        this.fareStrategy = fareStrategy;
    }

    public double calculateFare(double distance, String cabClass) {
        double baseFare = fareStrategy.calculateBaseFare(distance);
        return CabClassModifier.applyCabClassPercentage(baseFare, cabClass);
    }
}
