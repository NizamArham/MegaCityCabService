package com.cabservice.controller;

import com.cabservice.model.Route;
import com.cabservice.service.RouteService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/routes")
public class RouteServlet extends HttpServlet {
    private final RouteService routeService = new RouteService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Read request body manually
        StringBuilder requestBody = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        // Extract fields (Basic string parsing since no JSON libraries are allowed)
        String data = requestBody.toString();
        String locationA = extractValue(data, "locationA");
        String locationB = extractValue(data, "locationB");
        String distanceStr = extractValue(data, "distance");

        if (locationA == null || locationB == null || distanceStr == null) {
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request data");
            return;
        }

        double distance;
        try {
            distance = Double.parseDouble(distanceStr);
        } catch (NumberFormatException e) {
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Distance must be a valid number");
            return;
        }

        // Create and save the route
        Route route = new Route(locationA, locationB, distance);
        boolean success = routeService.addRoute(route);

        if (success) {
            sendResponse(response, HttpServletResponse.SC_OK, "Route added successfully");
        } else {
            sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add route");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Route> routes = routeService.getAllRoutes();
        PrintWriter out = response.getWriter();

        out.write("[");
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            out.write(String.format("{\"id\":%d, \"locationA\":\"%s\", \"locationB\":\"%s\", \"distance\":%.2f}",
                    route.getId(), route.getLocationA(), route.getLocationB(), route.getDistance()));
            if (i < routes.size() - 1) {
                out.write(",");
            }
        }
        out.write("]");
        out.flush();
    }

    // Utility method to extract JSON-like key-value pairs
    private String extractValue(String data, String key) {
        String searchKey = "\"" + key + "\":\"";
        int start = data.indexOf(searchKey);
        if (start == -1) return null;

        start += searchKey.length();
        int end = data.indexOf("\"", start);
        if (end == -1) return null;

        return data.substring(start, end);
    }

    // Utility method to send JSON response
    private void sendResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        PrintWriter out = response.getWriter();
        out.write("{\"message\":\"" + message + "\"}");
        out.flush();
    }
}
