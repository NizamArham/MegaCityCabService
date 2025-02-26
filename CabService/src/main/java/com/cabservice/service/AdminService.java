package com.cabservice.service;

import com.cabservice.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class AdminService {

    public int getCabCount() {
        int count = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS cab_count FROM vehicles")) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cab_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public int getDriverCount() {
        int count = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS driver_count FROM users WHERE account_type = 'driver'")) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("driver_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}