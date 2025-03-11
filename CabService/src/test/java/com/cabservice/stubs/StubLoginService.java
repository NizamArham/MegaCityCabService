package com.cabservice.stubs;

public class StubLoginService {
    private boolean loginSuccess;

    public StubLoginService(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public String authenticateUser(String email, String password) {
        if (loginSuccess) {
            return "{\"status\":\"success\", \"message\":\"Login successful\"}";
        } else {
            return "{\"status\":\"error\", \"message\":\"Invalid email or password\"}";
        }
    }
}
