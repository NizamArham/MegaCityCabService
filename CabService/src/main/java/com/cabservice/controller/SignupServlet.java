package com.cabservice.controller;

import com.cabservice.service.UserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Read JSON data from request
        StringBuilder jsonString = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonString.append(line);
        }

        // Parse JSON to Map
        Map<String, String> jsonData = parseJson(jsonString.toString());

        String role = jsonData.get("role");
        String firstName = jsonData.get("firstName");
        String lastName = jsonData.get("lastName");
        String nic = jsonData.get("nic");
        String tp = jsonData.get("tp");
        String email = jsonData.get("email");
        String password = jsonData.get("password");
        String assignedVehicle = "none";

        if (firstName == null || lastName == null || nic == null || tp == null || email == null || password == null || role == null) {
            out.write("{\"status\":\"error\", \"message\":\"All fields are required\"}");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            return;
        }


        if (userService == null) {
            userService = new UserService();
        }

        UserService userService = new UserService();
        if (userService.isUserExists(email, nic, tp)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 status code
            out.write("{\"status\":\"error\", \"message\":\"Email, NIC, or TP already in use\"}");
            return;
        }

        if (userService.createUser(role, firstName, lastName, nic, tp, email, password, assignedVehicle , "active")) {
            out.write("{\"status\":\"success\", \"message\":\"Signup successful\"}");
            response.setStatus(HttpServletResponse.SC_OK); // OK
        } else {
            out.write("{\"status\":\"error\", \"message\":\"Signup failed\"}");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
        }

    }

    private Map<String, String> parseJson(String json) {
        Map<String, String> data = new HashMap<>();
        json = json.replaceAll("[{}\"]", "");
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] entry = pair.split(":");
            if (entry.length == 2) {
                data.put(entry[0].trim(), entry[1].trim());
            }
        }
        return data;
    }


}
