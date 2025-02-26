package com.cabservice.controller;

import com.cabservice.service.VehicleService;
import com.cabservice.model.Vehicle;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/vehicle")
public class VehicleServlet extends HttpServlet {
    private final VehicleService vehicleService = new VehicleService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestData = getRequestData(request);

        String brand = extractJsonValue(requestData, "brand");
        String model = extractJsonValue(requestData, "model");
        String fuelType = extractJsonValue(requestData, "fuelType");
        String powerCapacityStr = extractJsonValue(requestData, "powersourceCapacity");
        String seatCapacityStr = extractJsonValue(requestData, "seatCapacity");
        String color = extractJsonValue(requestData, "color");
        String numberPlate = extractJsonValue(requestData, "numberPlate");
        String cabClass = extractJsonValue(requestData, "cabClass");
        String vehicleType = extractJsonValue(requestData, "vehicleType");

        if (!vehicleService.isValidNumberPlate(numberPlate)) {
            sendJsonResponse(response, "error", "Invalid number plate format. Expected format: XX-1234 or XXX-1234.");
            return;
        }

        int powerCapacity = Integer.parseInt(powerCapacityStr);
        int seatCapacity = Integer.parseInt(seatCapacityStr);

        boolean isAdded = vehicleService.saveVehicle(brand, model, fuelType, powerCapacity, color, numberPlate, seatCapacity, cabClass, vehicleType);
        if (isAdded) {
            sendJsonResponse(response, "success", "Vehicle added successfully!");
        } else {
            sendJsonResponse(response, "error", "Failed to add vehicle");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();

        // Convert the list of vehicles to JSON
        StringBuilder jsonResponse = new StringBuilder("[");
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            jsonResponse.append("{")
                    .append("\"id\": ").append(vehicle.getId()).append(", ")
                    .append("\"brand\": \"").append(vehicle.getBrand()).append("\", ")
                    .append("\"model\": \"").append(vehicle.getModel()).append("\", ")
                    .append("\"fuelType\": \"").append(vehicle.getFuelType()).append("\", ")
                    .append("\"powersourceCapacity\": ").append(vehicle.getPowerSourceCapacity()).append(", ")
                    .append("\"color\": \"").append(vehicle.getColor()).append("\", ")
                    .append("\"numberPlate\": \"").append(vehicle.getNumberPlate()).append("\", ")
                    .append("\"seatCapacity\": ").append(vehicle.getSeatCapacity()).append(", ")
                    .append("\"cabClass\": \"").append(vehicle.getCabClass()).append("\", ")
                    .append("\"vehicleType\": \"").append(vehicle.getVehicleType()).append("\", ")
                    .append("\"status\": \"").append(vehicle.getStatus()).append("\"")
                    .append("}");
            if (i < vehicles.size() - 1) {
                jsonResponse.append(", ");
            }
        }
        jsonResponse.append("]");

        // Send the JSON response directly
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestData = getRequestData(request);

        // Extract vehicle details from the request
        String idStr = extractJsonValue(requestData, "id");
        String brand = extractJsonValue(requestData, "brand");
        String model = extractJsonValue(requestData, "model");
        String fuelType = extractJsonValue(requestData, "fuelType");
        String powerCapacityStr = extractJsonValue(requestData, "powersourceCapacity");
        String seatCapacityStr = extractJsonValue(requestData, "seatCapacity");
        String color = extractJsonValue(requestData, "color");
        String numberPlate = extractJsonValue(requestData, "numberPlate");
        String cabClass = extractJsonValue(requestData, "cabClass");
        String vehicleType = extractJsonValue(requestData, "vehicleType");

        // Validate the number plate format
        if (!vehicleService.isValidNumberPlate(numberPlate)) {
            sendJsonResponse(response, "error", "Invalid number plate format. Expected format: XX-1234 or XXX-1234.");
            return;
        }

        // Parse numeric fields
        int id = Integer.parseInt(idStr);
        int powerCapacity = Integer.parseInt(powerCapacityStr);
        int seatCapacity = Integer.parseInt(seatCapacityStr);

        // Update the vehicle
        boolean isUpdated = vehicleService.updateVehicle(id, brand, model, fuelType, powerCapacity, color, numberPlate, seatCapacity, cabClass, vehicleType);
        if (isUpdated) {
            sendJsonResponse(response, "success", "Vehicle updated successfully!");
        } else {
            sendJsonResponse(response, "error", "Failed to update vehicle.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Extract the vehicle ID from the request
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            sendJsonResponse(response, "error", "Vehicle ID is required.");
            return;
        }

        int id = Integer.parseInt(idStr);

        // Delete the vehicle
        boolean isDeleted = vehicleService.deleteVehicle(id);
        if (isDeleted) {
            sendJsonResponse(response, "success", "Vehicle deleted successfully!");
        } else {
            sendJsonResponse(response, "error", "Failed to delete vehicle.");
        }
    }

    private String getRequestData(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    private String extractJsonValue(String requestData, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = requestData.indexOf(searchKey);
        if (startIndex != -1) {
            startIndex += searchKey.length();
            int endIndex = requestData.indexOf("\"", startIndex);
            if (endIndex != -1) {
                return requestData.substring(startIndex, endIndex);
            }
        }
        return null;
    }

    private void sendJsonResponse(HttpServletResponse response, String status, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = String.format("{\"status\":\"%s\", \"message\":\"%s\"}", status, message);
        response.getWriter().write(jsonResponse);
    }
}