package com.cabservice.controller;

import com.cabservice.service.BookingService;
import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/book")
public class BookingServlet extends HttpServlet {
    private BookingService bookingService = new BookingService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String pickUpAddress = request.getParameter("pickUpAddress");
        String dropAddress = request.getParameter("dropAddress");
        String cabClass = request.getParameter("cabClass");
        String vehicleType = request.getParameter("vehicleType");

        if (pickUpAddress == null || dropAddress == null || cabClass == null || vehicleType == null) {
            out.print("{\"status\":\"error\",\"message\":\"Missing required fields\"}");
            return;
        }

        boolean success = bookingService.saveBooking(pickUpAddress, dropAddress, cabClass, vehicleType);

        if (success) {
            out.print("{\"status\":\"success\",\"message\":\"Booking saved successfully\"}");
        } else {
            out.print("{\"status\":\"error\",\"message\":\"Database error: Could not save booking\"}");
        }
    }
}
