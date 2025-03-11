package com.cabservice.controller;

import com.cabservice.stubs.StubUserService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SignupServletTest {

    private SignupServlet servlet;
    private StubUserService userService;

    @org.junit.jupiter.api.BeforeEach // JUnit 5's setup method
    public void setUp() throws Exception {
        // Initialize servlet and stubs
        servlet = new SignupServlet();
        userService = new StubUserService(false, true);
        servlet.setUserService(userService); // Inject the stub service
    }

    @Test
    public void testDoPost_Success() throws Exception {
        // Sample JSON data for the signup request
        String json = "{\"role\":\"passenger\", \"firstName\":\"nizam\", \"lastName\":\"mohammad\", \"nic\":\"123496789V\", \"tp\":\"0771034567\", \"email\":\"n@example.com\", \"password\":\"password123\"}";

        // Create a connection to the URL
        URL url = new URL("http://localhost:8080/CabService/signup");
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

        // Assert the response
        assertEquals(200, responseCode);  // Check expected vs. actual response code
    }

    @Test
    public void testDoPost_MissingFields() throws Exception {
        // Mock request with missing fields
        String json = "{\"role\":\"passenger\", \"firstName\":\"nizam\"}"; // Missing lastName, nic, tp, email, password

        // Create a connection to the URL
        URL url = new URL("http://localhost:8080/CabService/signup");
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

        // Verify the response content includes the error message for missing fields
        assertTrue(responseContent.contains("\"status\":\"error\""));
        assertTrue(responseContent.contains("\"message\":\"All fields are required\""));
    }

    @Test
    public void testDoPost_UserExists() throws Exception {
        // Initialize servlet
        servlet = new SignupServlet();
        userService = new StubUserService(true, false); // Simulating user already exists
        servlet.setUserService(userService); // Set userService

        // Prepare test JSON request
        String json = "{\"role\":\"user\", \"firstName\":\"John\", \"lastName\":\"Doe\", \"nic\":\"200210602125\", \"tp\":\"0774470174\", \"email\":\"nizam@example.com\", \"password\":\"password123\"}";

        // Create connection
        URL url = new URL("http://localhost:8080/CabService/signup");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Get response code (should be 400 if user exists)
        int responseCode = connection.getResponseCode();
        assertEquals(400, responseCode); // Ensure we get 400

        // Read error response (since it's a failure case)
        String responseContent;
        try (InputStream errorStream = connection.getErrorStream()) {
            responseContent = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Validate response contains expected error message
        assertTrue(responseContent.contains("\"status\":\"error\""));
        assertTrue(responseContent.contains("\"message\":\"Email, NIC, or TP already in use\""));
    }
}
