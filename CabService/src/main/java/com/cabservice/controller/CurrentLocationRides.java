package com.cabservice.controller;

import com.cabservice.model.Ride;
import com.cabservice.service.RideService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/CurrentLocationRides")
public class CurrentLocationRides extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Read JSON data from request
        StringBuilder jsonString = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonString.append(line);
        }

        // Parse JSON to Map
        Map<String, String> jsonData = parseJson(jsonString.toString());

        // Extract values from JSON
        String driverEmail = jsonData.get("userEmail");
        String location = jsonData.get("location");

        List<Ride> availableRides = new ArrayList<>();

        if (driverEmail != null && !driverEmail.isEmpty() && location != null && !location.isEmpty()) {
            RideService rideService = new RideService();
            availableRides = rideService.getRidesForLocation(location,driverEmail);
        }

        out.write(JsonData.get(availableRides));
    }

    private Map<String, String> parseJson(String json) {
        Map<String, String> data = new HashMap<>();
        json = json.replaceAll("[{}\"]", "");
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] entry = pair.split(":");
            if (entry.length == 2) {
                data.put(entry[0].trim(), entry[1].trim());
            }
        }
        return data;
    }

    public static class JsonData {
        public static String get(List<Ride> rides) {
            if (rides == null || rides.isEmpty()) {
                return "[]";
            }

            StringBuilder json = new StringBuilder();
            json.append("[");

            for (int i = 0; i < rides.size(); i++) {
                Ride ride = rides.get(i);
                json.append("{")
                        .append("\"id\": \"").append(ride.getRideId()).append("\",")
                        .append("\"driver\": \"").append(ride.getDriverName()).append("\",")
                        .append("\"location\": \"").append(ride.getDriverLocation()).append("\",")
                        .append("\"vehicle\": \"").append(ride.getVehicle()).append("\",")
                        .append("\"passengerName\": \"").append(ride.getPassengerName()).append("\",")
                        .append("\"pickupLocation\": \"").append(ride.getPickupLocation()).append("\",")
                        .append("\"destination\": \"").append(ride.getDestination()).append("\",")
                        .append("\"passengerContact\": \"").append(ride.getPassengerContact()).append("\",")
                        .append("\"passengerEmail\": \"").append(ride.getPassengerEmail()).append("\"")
                        .append("}");
                if (i < rides.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            return json.toString();
        }
    }
}
