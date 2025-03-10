package com.cabservice.controller;

import com.cabservice.service.BookingService;
import com.cabservice.service.RideService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/bookingstatus")
public class getRideStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");


        // Get booking ID from request
        String bookingId = request.getParameter("bookingID");

        System.out.println("Status checker: " + bookingId);

        if (bookingId == null) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Booking ID required\"}");
            return;
        }

        // Fetch the latest ride status
        String rideStatus = RideService.getRideStatus(bookingId);
        response.getWriter().write("{\"status\":\"success\",\"rideStatus\":\"" + rideStatus + "\"}");
    }
}
