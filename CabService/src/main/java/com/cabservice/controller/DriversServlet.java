package com.cabservice.controller;

import com.cabservice.model.User;
import com.cabservice.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/drivers")
public class DriversServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> users = userService.getAllDrivers();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Convert the list of users to JSON and send it as the response
        String jsonResponse = convertUsersToJson(users);
        response.getWriter().write(jsonResponse);
    }


    // Convert list of users to JSON
    private String convertUsersToJson(List<User> users) {
        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            json.append("{")
                    .append("\"firstName\":\"").append(user.getFirstName()).append("\",")
                    .append("\"lastName\":\"").append(user.getLastName()).append("\",")
                    .append("\"nic\":\"").append(user.getNic()).append("\",")
                    .append("\"tp\":\"").append(user.getTp()).append("\",")
                    .append("\"email\":\"").append(user.getEmail()).append("\",")
                    .append("\"assignedVehicle\":\"").append(user.getAssignedVehicle()).append("\",")
                    .append("\"accountStatus\":\"").append(user.getAccountStatus()).append("\",")
                    .append("\"role\":\"").append(user.getRole()).append("\"");

            // Add a comma between users, but not after the last one
            if (i < users.size() - 1) {
                json.append("},");
            } else {
                json.append("}");
            }
        }

        json.append("]");
        return json.toString();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

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
        String accountStatus = "active";
        String assignedVehicle = jsonData.get("assignedVehicle");

        if (firstName == null || lastName == null || nic == null || tp == null || email == null || password == null || role == null) {
            out.write("{\"status\":\"error\", \"message\":\"All fields are required\"}");
            return;
        }

        UserService userService = new UserService();
        if (userService.isUserExists(email, nic, tp)) {
            out.write("{\"status\":\"error\", \"message\":\"Oops! It looks like this email, NIC, or phone number is already in use. Try using another one.\"}");
            return;
        }

        if (userService.createUser(role, firstName, lastName, nic, tp, email, password, assignedVehicle, accountStatus)) {
            out.write("{\"status\":\"success\", \"message\":\"Driver Added Successfully\"}");
        } else {
            out.write("{\"status\":\"error\", \"message\":\"Signup failed\"}");
        }
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Read JSON data from request
        StringBuilder jsonString = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonString.append(line);
        }

        // Parse JSON to get the email
        Map<String, String> jsonData = parseJson(jsonString.toString());
        String email = jsonData.get("email");

        if (email == null || email.isEmpty()) {
            out.write("{\"status\":\"error\", \"message\":\"Email is required\"}");
            return;
        }

        // Call service method to delete the driver
        boolean isDeleted = userService.deleteDriverByEmail(email);

        if (isDeleted) {
            out.write("{\"status\":\"success\", \"message\":\"Driver deleted successfully\"}");
        } else {
            out.write("{\"status\":\"error\", \"message\":\"Failed to delete driver\"}");
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