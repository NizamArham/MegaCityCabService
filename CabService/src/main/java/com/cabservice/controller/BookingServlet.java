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

@WebServlet({"/createbooking", "/booking/status/*"})  // Add a new path for status retrieval
public class BookingServlet extends HttpServlet {

    private BookingService bookingService = new BookingService();
    private RideStatusService rideStatusService = new RideStatusService();

    // Handle POST request to create booking
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
        String bookingStatus = jsonData.getOrDefault("bookingStatus", "Requested"); // Default status
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
            rideStatusService.addPassengerObserver(String.valueOf(bookingId),passengerNotification);

            // Set the ride status to "requested" and notify the observer
            rideStatusService.passengerRequestsRide(String.valueOf(bookingId));

            System.out.println("Booking created successfully with ID: " + bookingId);

            out.write("{\"status\":\"success\",\"message\":\"Booking saved successfully\",\"bookingId\":\"" + bookingId + "\"}");
        } else {
            out.write("{\"status\":\"error\",\"message\":\"Database error: Could not save booking\"}");
            System.out.println("Error: Could not save booking");
        }
    }

    // Handle GET request to retrieve booking status by bookingId
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Extract bookingId from URL path
        String pathInfo = request.getPathInfo(); // Get the part after /booking/status/
        if (pathInfo == null || pathInfo.isEmpty()) {
            out.write("{\"status\":\"error\",\"message\":\"Booking ID is missing\"}");
            return;
        }
        String bookingIdString = pathInfo.substring(1); // Remove the leading "/"

        // Validate bookingId
        if (bookingIdString.isEmpty()) {
            out.write("{\"status\":\"error\",\"message\":\"Invalid booking ID\"}");
            return;
        }

        try {
            int bookingId = Integer.parseInt(bookingIdString);

            // Retrieve the status of the booking
            String status = BookingService.getBookingStatus(String.valueOf(bookingId));

            if (status != null) {
                // Send the booking status in the response
                out.write("{\"status\":\"success\",\"bookingId\":\"" + bookingId + "\",\"rideStatus\":\"" + status + "\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"Booking ID not found\"}");
            }
        } catch (NumberFormatException e) {
            out.write("{\"status\":\"error\",\"message\":\"Invalid booking ID format\"}");
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