package com.cabservice.controller;

import com.cabservice.model.User;
import com.cabservice.service.LoginService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private LoginService loginService = new LoginService();  // Service Layer

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Read request data
        String requestData = getRequestData(request);
        String email = extractJsonValue(requestData, "email");
        String password = extractJsonValue(requestData, "password");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Create User object to pass to service
        User user = new User(email, password);

        // Call service method to validate user credentials
        String responseMessage = loginService.authenticateUser(user);

        out.write(responseMessage);
        out.flush();
    }

    private String getRequestData(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        return requestBody.toString();
    }

    private String extractJsonValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\":\"");
        if (keyIndex == -1) return "";
        int startIndex = keyIndex + key.length() + 4;
        int endIndex = json.indexOf("\"", startIndex);
        return endIndex > startIndex ? json.substring(startIndex, endIndex) : "";
    }
}
