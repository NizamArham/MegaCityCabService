package com.cabservice.controller;

import com.cabservice.service.RouteService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

@WebServlet("/fetchlocations")
public class CurrentLocation extends HttpServlet {

    private RouteService routeService = new RouteService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get all unique locations sorted alphabetically
        Set<String> locations = new TreeSet<>(routeService.getAllLocations());

        // Create JSON response
        StringBuilder jsonResponse = new StringBuilder();
        jsonResponse.append("[");

        int i = 0;
        for (String location : locations) {
            jsonResponse.append("\"").append(location).append("\"");
            if (i < locations.size() - 1) {
                jsonResponse.append(",");
            }
            i++;
        }

        jsonResponse.append("]");

        // Send response
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }

//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        // Read request body
//        StringBuilder requestBody = new StringBuilder();
//        try (BufferedReader reader = request.getReader()) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                requestBody.append(line);
//            }
//        }
//
//        // Convert raw request body to String
//        String requestData = requestBody.toString();
//
//        // Extract userEmail and location manually
//        String userEmail = extractValue(requestData, "userEmail");
//        String location = extractValue(requestData, "location");
//
//        // Debugging
//        System.out.println("Received request: User = " + userEmail + ", Location = " + location);
//
//        // Check for valid input
//        if (userEmail == null || location == null) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().write("{\"status\": \"error\", \"message\": \"Invalid request format\"}");
//            return;
//        }
//
//        // Save location (this method should be implemented in RouteService)
//        //boolean isUpdated = routeService.updateUserLocation(userEmail, location);
//
//        // Send response
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//
//        if (isUpdated) {
//            response.getWriter().write("{\"status\": \"success\", \"message\": \"Location updated\"}");
//        } else {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            response.getWriter().write("{\"status\": \"error\", \"message\": \"Failed to update location\"}");
//        }
//    }

    /**
     * Extracts a value from a JSON-like string manually.
     * Assumes format: {"key": "value", "anotherKey": "value"}
     */
    private String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }

        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) {
            return null;
        }

        return json.substring(startIndex, endIndex);
    }

}
