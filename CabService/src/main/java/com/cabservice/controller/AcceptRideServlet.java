package com.cabservice.controller;

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

        // Parse JSON to extract parameters
        String vehicle = null;
        String driverEmail = null;
        String bookingId = null;

        // Simple parsing assuming JSON structure is flat and keys are "vehicle", "driverEmail", "bookingId"
        String json = jsonString.toString();
        if (json.contains("\"vehicle\"")) {
            vehicle = extractJsonValue(json, "vehicle");
        }
        if (json.contains("\"driverEmail\"")) {
            driverEmail = extractJsonValue(json, "driverEmail");
        }
        if (json.contains("\"bookingId\"")) {
            bookingId = extractJsonValue(json, "bookingId");
        }

        // Validate input
        if (driverEmail == null || vehicle == null || bookingId == null) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Missing required fields\"}");
            return;
        }

        // Process the booking
        boolean success = bookingService.acceptRide(bookingId, driverEmail, vehicle);

        // Send response
        if (success) {
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Booking accepted successfully\"}");
        } else {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Could not accept the booking\"}");
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
