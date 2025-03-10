package com.cabservice.model;

public abstract class User {
    private String firstName;
    private String lastName;
    private String nic;
    private String tp;
    private String email;
    private String password;
    private String assignedVehicle;
    private String accountStatus;

    public User(String firstName, String lastName, String nic, String tp, String email, String password, String assignedVehicle , String accountStatus) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nic = nic;
        this.tp = tp;
        this.email = email;
        this.password = password;
        this.assignedVehicle = assignedVehicle;
        this.accountStatus = accountStatus;
    }

    public User(String firstName, String tp, String email) {
        this.firstName = firstName;
        this.tp = tp;
        this.email = email;
    }

    // Getters and Setters

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public abstract String getRole();


    public String getAssignedVehicle() {
        return assignedVehicle;
    }

    public void setAssignedVehicle(String assignedVehicle) {
        this.assignedVehicle = assignedVehicle;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
