package com.cabservice.strategy;

public class FareStrategyFactory {
    public static FareStrategy getFareStrategy(String vehicleType) {
        switch (vehicleType.toLowerCase()) {
            case "tuk": return new TukTukFareStrategy();
            case "car": return new CarFareStrategy();
            case "van": return new VanFareStrategy();
            default: throw new IllegalArgumentException("Invalid vehicle type: " + vehicleType);
        }
    }
}
