package com.cabservice.controller;

import com.cabservice.observer.PassengerNotification;
import com.cabservice.observer.RideStatusService;
import com.cabservice.service.BookingService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/acceptRide")
public class AcceptRideServlet extends HttpServlet {

    private BookingService bookingService = new BookingService();
    private RideStatusService rideStatusService = new RideStatusService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Read JSON data from the request body
        StringBuilder jsonString = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
        }

        // Extract JSON values manually
        String json = jsonString.toString().trim();
        String vehicle = extractJsonValue(json, "vehicle");
        String driverEmail = extractJsonValue(json, "driverEmail");
        String bookingId = extractJsonValue(json, "bookingId");

        // Validate input
        if (vehicle == null || driverEmail == null || bookingId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Missing required fields\"}");
            return;
        }

        try {
            // Register observer
            PassengerNotification passengerNotification = new PassengerNotification(bookingId);
            rideStatusService.addPassengerObserver(bookingId,passengerNotification);


            // Set ride status
            rideStatusService.driverAcceptsRide(bookingId);

            // Process booking
            boolean success = bookingService.acceptRide(bookingId, driverEmail, vehicle);

            if (success) {
                response.getWriter().write("{\"status\":\"success\",\"message\":\"Booking accepted successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Could not accept the booking\"}");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print error logs for debugging
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Internal server error\"}");
        }
    }

    // Helper method to extract value from JSON string
    private String extractJsonValue(String json, String key) {
        String keyValuePair = "\"" + key + "\":\"";
        int startIndex = json.indexOf(keyValuePair);
        if (startIndex != -1) {
            startIndex += keyValuePair.length();
            int endIndex = json.indexOf("\"", startIndex);
            if (endIndex != -1) {
                return json.substring(startIndex, endIndex);
            }
        }
        return null;
    }
}