package com.cabservice.controller;

import com.cabservice.model.Vehicle;
import com.cabservice.service.VehicleService;

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

@WebServlet("/vehicle")
public class VehicleServlet extends HttpServlet {

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

        String brand = jsonData.get("brand");
        String model = jsonData.get("model");
        String engineCapacity = jsonData.get("engineCapacity");
        String color = jsonData.get("color");
        String numberPlate = jsonData.get("numberPlate");
        String seatCapacity = jsonData.get("seatCapacity");
        String vehicleType = jsonData.get("vehicleType");


        if (brand == null || model == null || engineCapacity == null || color == null || numberPlate == null || seatCapacity == null || vehicleType == null) {
            out.write("{\"status\":\"error\", \"message\":\"All fields are required\"}");
            return;
        }



        VehicleService vehicleService = new VehicleService();
        if (vehicleService.isVehicleExists(numberPlate)) {
            out.write("{\"status\":\"error\", \"message\":\"Vehicle with this number plate already exists\"}");
            return;
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setEngineCapacity(engineCapacity);
        vehicle.setColor(color);
        vehicle.setNumberPlate(numberPlate);
        vehicle.setSeatCapacity(seatCapacity);
        vehicle.setVehicleType(vehicleType);

        if (vehicleService.saveVehicle(vehicle)) {
            out.write("{\"status\":\"success\", \"message\":\"Vehicle registered successfully\"}");
        } else {
            out.write("{\"status\":\"error\", \"message\":\"Vehicle registration failed\"}");
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        VehicleService vehicleService = new VehicleService();
        List<Vehicle> vehicles = vehicleService.getAllVehicles();

        // Convert the list of vehicles to JSON format
        String jsonResponse = VehicleService.convertListToJson(vehicles);
        out.write(jsonResponse);
    }
}
