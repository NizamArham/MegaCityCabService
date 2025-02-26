package com.cabservice.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.cabservice.service.AdminService;

@WebServlet("/adminoverview")
public class AdminOverviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminService adminService = new AdminService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Get the count data from the service
        int cabCount = adminService.getCabCount();
        int driverCount = adminService.getDriverCount();

        // Manually build the JSON response
        String jsonResponse = String.format(
                "{\"cabCount\": %d, \"driverCount\": %d}",
                cabCount, driverCount
        );

        out.print(jsonResponse);
        out.flush();
    }
}
