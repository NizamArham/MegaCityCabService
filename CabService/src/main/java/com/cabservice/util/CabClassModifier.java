package com.cabservice.util;

public class CabClassModifier {
    public static double applyCabClassPercentage(double baseFare, String cabClass) {
        double percentage = 0;

        switch (cabClass.toLowerCase()) {
            case "standard":
                percentage = 2;
                break;
            case "premium":
                percentage = 5;
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

