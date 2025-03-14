package com.cabservice.util;

public class CabClassModifier {
    public static double applyCabClassPercentage(double baseFare, String cabClass) {
        double percentage;

        switch (cabClass.toLowerCase()) {
            case "economy":
                percentage = 2;
                break;
            case "standard":
                percentage = 5;
                break;
            case "semi-luxury":
                percentage = 8;
                break;
            case "luxury":
                percentage = 10;
                break;
            default:
                percentage = 0;
        }

        return baseFare + (baseFare * (percentage / 100));
    }
}

