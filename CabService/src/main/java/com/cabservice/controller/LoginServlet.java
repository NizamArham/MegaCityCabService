package com.cabservice.controller;

import com.cabservice.service.LoginService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final LoginService loginService = new LoginService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestData = getRequestData(request);
        String email = extractJsonValue(requestData, "email");
        String password = extractJsonValue(requestData, "password");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            out.write("{\"status\":\"error\", \"message\":\"Email and Password are required\"}");
            out.flush();
            return;
        }

        String authResponse = loginService.authenticateUser(email, password);
        out.write(authResponse);
        out.flush();
    }

    private String getRequestData(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        return requestBody.toString();
    }

    private String extractJsonValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\":\"");
        if (keyIndex == -1) return null;
        int startIndex = keyIndex + key.length() + 4;
        int endIndex = json.indexOf("\"", startIndex);
        return endIndex > startIndex ? json.substring(startIndex, endIndex) : null;
    }
}
