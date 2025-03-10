package com.cabservice.controller;

import com.cabservice.model.Ride;
import com.cabservice.service.RideService;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.net.URLDecoder;
import java.util.Optional;

@WebServlet("/ongoingRide")
public class OngoingRideServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userEmail = URLDecoder.decode(request.getParameter("email"), "UTF-8");
        System.out.println("Received email: " + userEmail);  // Log the email for debugging

        if (userEmail == null || userEmail.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Email parameter is required");
            return;
        }

        // Assuming RideService has a method that fetches the ride info from the DB
        Optional<Ride> ongoingRide = RideService.getOngoingRideByEmailForUser(userEmail);


        if (ongoingRide.isPresent()) {
            // Set the response type
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Write the ride information as a JSON object
            String jsonResponse = JsonData.get(ongoingRide.get());
            response.getWriter().write(jsonResponse);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("No ongoing ride found for the provided email");
        }
    }

    // JsonData class for handling the conversion of a Ride object to a JSON string
    public static class JsonData {
        public static String get(Ride ride) {
            if (ride == null) {
                return "{}";  // Return empty JSON object if ride is null
            }

            // Constructing JSON representation of a single Ride object
            return "{" +
                    "\"rideId\": " + ride.getRideId() + ", " +
                    "\"pickUpAddress\": \"" + ride.getPickupLocation() + "\", " +
                    "\"dropAddress\": \"" + ride.getDestination() + "\", " +
                    "\"createdAt\": \"" + ride.getBookedTime() + "\", " +
                    "\"updatedAt\": \"" + ride.getUpdatedTime() + "\", " +
                    "\"vehicleClass\": \"" + ride.getVehicleClass() + "\", " +
                    "\"vehicleType\": \"" + ride.getVehicleType() + "\", " +
                    "\"vehicleNumber\": \"" + ride.getVehicle() + "\", " +
                    "\"driverName\": \"" + ride.getDriverName() + "\", " +
                    "\"driverTel\": \"" + ride.getDriverTel() + "\", " +
                    "\"distance\": \"" + ride.getDistance() + "\", " +
                    "\"fare\": " + ride.getFare() + ", " +
                    "\"status\": \"" + ride.getBookingStatus() + "\"" +
                    "}";
        }
    }
}
