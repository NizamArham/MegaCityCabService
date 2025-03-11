package com.cabservice.stubs;

import com.cabservice.service.UserService;

public class StubUserService extends UserService {
    private boolean userExists;
    private boolean signupSuccess;

    public StubUserService(boolean userExists, boolean signupSuccess) {
        this.userExists = userExists;
        this.signupSuccess = signupSuccess;
    }

    @Override
    public boolean isUserExists(String email, String nic, String tp) {
        return userExists;
    }

    @Override
    public boolean createUser(String role, String firstName, String lastName, String nic, String tp, String email, String password, String assignedVehicle, String accountStatus) {
        return signupSuccess;
    }
}
