package com.cabservice.controller;

import com.cabservice.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginServletTest {

    private LoginServlet servlet;
    private LoginService loginService;

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize servlet and service
        servlet = new LoginServlet();
        loginService = new LoginService();
    }

    @Test
    public void testDoPost_Success() throws Exception {
        // Sample JSON data for the login request
        String json = "{\"email\":\"hajaraf@gmail.com\", \"password\":\"1234\"}";

        // Create a connection to the URL
        URL url = new URL("http://localhost:8080/CabService/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        // Set headers
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Write the JSON data
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Get response code
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read response body
        String responseBody;
        try (InputStream is = responseCode < 400 ? connection.getInputStream() : connection.getErrorStream()) {
            responseBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        System.out.println("Response Body: " + responseBody);

        // Assert the response code and message
        assertEquals(200, responseCode);  // Successful login should return 200
        assertTrue(responseBody.contains("\"status\": \"success\""));
        assertTrue(responseBody.contains("\"acc_type\": \"passenger\""));
        assertTrue(responseBody.contains("\"token\":"));

    }

    @Test
    public void testDoPost_MissingFields() throws Exception {
        // Mock request with missing fields
        String json = "{\"email\":\"nizam@example.com\"}"; // Missing password

        // Create a connection to the URL
        URL url = new URL("http://localhost:8080/CabService/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        // Set the Content-Type to application/json
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Write the JSON data to the request body
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Get the response code (400 means bad request due to missing fields)
        int responseCode = connection.getResponseCode();

        // Verify the response code is 400 (Bad Request)
        assertEquals(400, responseCode);

        // Get the error content (for a 400 error, use getErrorStream())
        String responseContent;
        try (InputStream errorStream = connection.getErrorStream()) {
            if (errorStream != null) {
                responseContent = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
            } else {
                responseContent = "";
            }
        }

        System.out.println("Response Content: " + responseContent);
        // Verify the response content includes the error message for missing fields
        assertTrue(responseContent.contains("\"status\":\"error\""));
        assertTrue(responseContent.contains("\"message\":\"Email and Password are required\""));
    }

    @Test
    public void testDoPost_InvalidCredentials() throws Exception {
        // Prepare test JSON request with incorrect credentials
        String json = "{\"email\":\"nizam@example.com\", \"password\":\"wrongpassword\"}";

        // Create connection
        URL url = new URL("http://localhost:8080/CabService/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Get response code (should be 401 for invalid credentials)
        int responseCode = connection.getResponseCode();
        assertEquals(401, responseCode); // Ensure we get 401 for invalid credentials

        // Read error response (since it's a failure case)
        String responseContent;
        try (InputStream errorStream = connection.getErrorStream()) {
            responseContent = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        responseContent = responseContent.trim(); // Remove any leading/trailing whitespace

        // Print the actual response to help debug
        System.out.println("Actual Response: " + responseContent);

        // Use assertEquals to compare the full response
        String expectedResponse = "{\"status\": \"error\", \"message\": \"Invalid credentials\"}";
        assertEquals(expectedResponse, responseContent); // Ensure the entire JSON matches
    }


}
