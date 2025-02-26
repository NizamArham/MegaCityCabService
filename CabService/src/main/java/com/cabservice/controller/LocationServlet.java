package com.cabservice.controller;

import com.cabservice.service.RouteService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@WebServlet("/locations")
public class LocationServlet extends HttpServlet {

    private RouteService routeService = new RouteService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");

        // Get matching locations from RouteService
        Set<String> locations = routeService.getMatchingLocations(query);

        // Manually create the JSON response
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

        // Send the response
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }
}
