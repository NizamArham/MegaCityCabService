package com.cabservice.controller;

import com.cabservice.model.Ride;
import com.cabservice.service.RideService;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/pastRides")
public class PastRidesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userEmail = URLDecoder.decode(request.getParameter("email"), "UTF-8");
        System.out.println("Received email to check past rides: " + userEmail);

        if (userEmail == null || userEmail.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Email parameter is required");
            return;
        }

        // Assuming RideService has a method that fetches past rides from the DB
        List<Ride> pastRides = RideService.getPastRidesByEmailForUser(userEmail);

        if (pastRides != null && !pastRides.isEmpty()) {
            // Set the response type
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Convert the list of rides to a JSON array
            String jsonResponse = JsonData.get(pastRides);
            response.getWriter().write(jsonResponse);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("No past rides found for the provided email");
        }
    }

    // JsonData class for handling the conversion of a list of Ride objects to a JSON string
    public static class JsonData {
        public static String get(List<Ride> rides) {
            if (rides == null || rides.isEmpty()) {
                return "[]";  // Return empty JSON array if no past rides
            }

            // Constructing JSON representation of a list of rides
            return rides.stream()
                    .map(PastRidesServlet.JsonData::get)
                    .collect(Collectors.joining(",", "[", "]"));
        }

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
                    "\"fare\": \"" + ride.getFare() + "\", " +
                    "\"status\": \"" + ride.getBookingStatus() + "\", " +
                    "\"paymentstatus\": \"" + ride.getPaymentStatus() + "\", " +
                    "\"paymentmethod\": \"" + ride.getPaymentMethod() + "\"" +
                    "}";
            }
        }
}
