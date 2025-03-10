package com.cabservice.controller;

import com.cabservice.observer.PassengerNotification;
import com.cabservice.observer.RideStatusService;
import com.cabservice.service.BookingService;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


@WebServlet("/createbooking")
public class BookingServlet extends HttpServlet {

    private BookingService bookingService = new BookingService();
    private RideStatusService rideStatusService = new RideStatusService();

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
        String pickUpAddress = jsonData.get("pickUpAddress");
        String dropAddress = jsonData.get("dropAddress");
        String cabClass = jsonData.get("cabClass");
        String vehicleType = jsonData.get("vehicleType");
        String bookingStatus = jsonData.getOrDefault("bookingStatus", "Pending"); // Default status
        String passengerEmail = jsonData.get("passengerEmail");

        // Validate input
        if (pickUpAddress == null || dropAddress == null || cabClass == null || vehicleType == null) {
            out.write("{\"status\":\"error\",\"message\":\"Missing required fields\"}");
            return;
        }

        // Save booking and get the generated booking ID
        int bookingId = bookingService.saveBooking(pickUpAddress, dropAddress, cabClass, vehicleType, bookingStatus, passengerEmail);

        if (bookingId > 0) {
            // After the booking is saved, create the observer and add it
            PassengerNotification passengerNotification = new PassengerNotification(String.valueOf(bookingId));
            rideStatusService.addPassengerObserver(passengerNotification);

            // Set the ride status to "requested" and notify the observer
            rideStatusService.passengerRequestsRide(String.valueOf(bookingId));

            out.write("{\"status\":\"success\",\"message\":\"Booking saved successfully\",\"bookingId\":\"" + bookingId + "\"}");
        } else {
            out.write("{\"status\":\"error\",\"message\":\"Database error: Could not save booking\"}");
            System.out.println("Error: Could not save booking");
        }
    }

    // Helper method to parse JSON string into a Map
    private Map<String, String> parseJson(String json) {
        Map<String, String> data = new HashMap<>();
        json = json.replaceAll("[{}\"]", ""); // Fixed the escaping issue
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] entry = pair.split(":");
            if (entry.length == 2) {
                data.put(entry[0].trim(), entry[1].trim());
            }
        }
        return data;
    }
}
