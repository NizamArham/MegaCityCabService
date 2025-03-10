import React, { useEffect, useState, useRef } from "react";
import { jwtDecode } from "jwt-decode";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import "../../css/adminhome.css";
import { useNavigate } from "react-router-dom";
import ProfileTag from "../profiletag";

const DriverMyrides = ({ accountType = "Driver" }) => {
  const [userEmail, setUserEmail] = useState("");
  const [sessionExpired, setSessionExpired] = useState(false);
  const navigate = useNavigate();
  const profileRef = useRef(null);

  useEffect(() => {
    const token = localStorage.getItem("authToken");

    if (!token) {
      setSessionExpired(true);
      setTimeout(() => {
        navigate("/login");
      }, 3000);
      return;
    }

    try {
      const decodedToken = jwtDecode(token);
      setUserEmail(decodedToken.email);
      if (decodedToken.acc_type) {
        accountType = decodedToken.acc_type;
      }
    } catch (error) {
      console.error("Invalid token", error);
    }

    const handleClickOutside = (event) => {
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        // Handle outside click if necessary
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [navigate]);

  const firstLetter = userEmail.charAt(0).toUpperCase();

  // Sample Ride Information
  const rideInfo = {
    pickupLocation: "Downtown",
    destinationName: "Central Park",
    bookedTime: "10:30 AM",
    vehicleType: "Car (Economy)",
    vehicleNo: "ABC-1234",
    passengerName: "John Doe",
    passengerContact: "+94XXXXXXXXX",
    passengerEmail: "john.doe@gmail.com",
    distance: "12.5 Km",
    estimatedFare: "1500 LKR",
    rideStatus: "Ongoing",
  };

  return (
    <div className="admin-container">
      {sessionExpired && (
        <div className="session-expired-message">
          <p>Your session has expired. Please log in again.</p>
        </div>
      )}

      {!sessionExpired && (
        <header className="admin-header d-flex justify-content-between align-items-center p-3">
          <div>
            <h1 className="admin-title">Ongoing Ride</h1>
            <p className="admin-subtext">See all the information about current ride</p>
          </div>
          <div className="admin-profile d-flex align-items-center" ref={profileRef}>
            <div className="profile-icon">{firstLetter}</div>
            <ProfileTag userEmail={userEmail} accountType={accountType} />
          </div>
        </header>
      )}

      <div className="container mt-4">
        {/* First Row */}
        <div className="row">
          {/* Small Cards */}
          <div className="col-md-3 mb-4">
            <div className="card small-card shadow-sm">
              <div className="card-body">
                {/* Icon Section */}
                <div className="card-icons d-flex justify-content-start">
                  <i className="bi bi-geo text-primary" style={{ fontSize: "1.5rem" }}></i>
                </div>

                {/* Ride Information */}
                <div className="mt-3">
                  {/* From */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      From
                    </p>
                    <p className="font-weight-bold mb-1" style={{ fontSize: "1.1rem", fontWeight: "1000" }}>
                      {rideInfo.pickupLocation}
                    </p>
                  </div>

                  {/* To */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      To
                    </p>
                    <h4 className="text-primary mb-1" style={{ fontSize: "1.5rem", fontWeight: "bold" }}>
                      {rideInfo.destinationName}
                    </h4>
                  </div>

                  {/* Booked Time */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Booked Time
                    </p>
                    <p className="font-weight-bold mb-1" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      {rideInfo.bookedTime}
                    </p>
                  </div>
                </div>
              </div>

              {/* Footer */}
              <div className="card-footernoBg text-center">
                <p className="subtext text-muted" style={{ fontSize: "1rem", fontWeight: "500" }}>
                  Pick & Drop
                </p>
              </div>
            </div>
          </div>

          {/* Small Cards - Booking Details */}
          <div className="col-md-3 mb-4">
            <div className="card small-card shadow-sm">
              <div className="card-body">
                {/* Icon Section */}
                <div className="card-icons d-flex justify-content-start">
                  <i className="bi bi-clock-history text-primary" style={{ fontSize: "1.5rem" }}></i>
                </div>

                {/* Booking Details */}
                <div className="mt-3">
                  {/* Last Updated */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Last Updated
                    </p>
                    <p className="font-weight-bold mb-1" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      {rideInfo.bookedTime}
                    </p>
                  </div>

                  {/* Vehicle Type (Cab Class) */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Vehicle
                    </p>
                    <h4 className="text-primary mb-1" style={{ fontSize: "1.5rem", fontWeight: "bold" }}>
                      {rideInfo.vehicleType}
                    </h4>
                  </div>

                  {/* Vehicle No */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Vehicle No
                    </p>
                    <h4 className="text-primary mb-1" style={{ fontSize: "1.5rem", fontWeight: "bold" }}>
                      {rideInfo.vehicleNo}
                    </h4>
                  </div>
                </div>
              </div>

              {/* Footer */}
              <div className="card-footernoBg text-center">
                <p className="subtext text-muted" style={{ fontSize: "1rem", fontWeight: "500" }}>
                  Booking Details
                </p>
              </div>
            </div>
          </div>

          {/* Small Cards - Passenger Details */}
          <div className="col-md-3 mb-4">
            <div className="card small-card shadow-sm">
              <div className="card-body">
                {/* Icon Section */}
                <div className="card-icons d-flex justify-content-start">
                  <i className="bi bi-person-badge text-primary" style={{ fontSize: "1.5rem" }}></i>
                </div>

                {/* Passenger Information */}
                <div className="mt-3">
                  {/* Passenger Name */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Passenger
                    </p>
                    <p className="font-weight-bold mb-1" style={{ fontSize: "1.1rem", fontWeight: "1000" }}>
                      {rideInfo.passengerName}
                    </p>
                  </div>

                  {/* Contact No */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Contact
                    </p>
                    <p className="font-weight-bold mb-1" style={{ fontSize: "1.1rem", fontWeight: "1000" }}>
                      {rideInfo.passengerContact}
                    </p>
                  </div>

                  {/* Contact No */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Email
                    </p>
                    <p className="font-weight-bold mb-1" style={{ fontSize: "1.1rem", fontWeight: "1000" }}>
                      {rideInfo.passengerEmail}
                    </p>
                  </div>
                </div>
              </div>

              {/* Footer */}
              <div className="card-footernoBg text-center">
                <p className="subtext text-muted" style={{ fontSize: "1rem", fontWeight: "500" }}>
                  Passenger Details
                </p>
              </div>
            </div>
          </div>

          {/* Small Cards - Ride Info */}
          <div className="col-md-2 mb-4">
            <div className="card small-card shadow-sm">
              <div className="card-body">
                {/* Icon Section */}
                <div className="card-icons d-flex justify-content-start">
                  <i className="bi bi-speedometer2 text-primary" style={{ fontSize: "1.5rem" }}></i>
                </div>

                {/* Ride Information */}
                <div className="mt-3">
                  {/* Distance */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Distance
                    </p>
                    <p className="font-weight-bold mb-1" style={{ fontSize: "1.1rem", fontWeight: "1000" }}>
                      {rideInfo.distance}
                    </p>
                  </div>

                  {/* Estimated Fare */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Est. Fare
                    </p>
                    <p className="font-weight-bold mb-1" style={{ fontSize: "1.1rem", fontWeight: "1000" }}>
                      {rideInfo.estimatedFare}
                    </p>
                  </div>

                  {/* Ride Status */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Status
                    </p>
                    <h4 className="text-success mb-1" style={{ fontSize: "1rem", fontWeight: "bold" }}>
                      {rideInfo.rideStatus}
                    </h4>
                  </div>
                </div>
              </div>

              {/* Footer */}
              <div className="card-footernoBg text-center">
                <p className="subtext text-muted" style={{ fontSize: ".9rem", fontWeight: "500" }}>
                  Ride Summary
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div>
        <h1 className="admin-title">My Rides</h1>
        <p className="admin-subtext">See all the information about current ride</p>
      </div>
    </div>
  );
};

export default DriverMyrides;
