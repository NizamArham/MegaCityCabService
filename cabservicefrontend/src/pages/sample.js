import React, { useEffect, useState, useRef } from "react";
import { jwtDecode } from "jwt-decode";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import "../../css/adminhome.css";
import { data, useNavigate } from "react-router-dom";
import ProfileTag from "../profiletag";


const PassengerMyRide = ({ accountType = "User" }) => {
  const [userEmail, setUserEmail] = useState("");
  const [rideDetails, setRideDetails] = useState(null);
  const [pastRides, setPastRides] = useState([]);
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
  }, [navigate]);

  useEffect(() => {
    if (!userEmail) return; 

    console.log("Fetching ride details for:", userEmail);
    const fetchRideDetails = async () => {
      const token = localStorage.getItem("authToken");

      try {
        const response = await fetch(`http://localhost:8080/CabService/ongoingRide?email=${encodeURIComponent(userEmail)}`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.ok) {
          const data = await response.json();
          setRideDetails(data); // Set the fetched data
        } else {
          console.error("Failed to fetch ride details");
          // If no ongoing ride found, try to fetch past rides
        fetchPastRides();
        }
      } catch (error) {
        console.error("Error fetching ride details:", error);
      }
    };

    const fetchPastRides = async () => {
        const token = localStorage.getItem("authToken");
        console.log("Fetching ride details for:", userEmail);
      
        try {
          const response = await fetch(`http://localhost:8080/CabService/pastRides?email=${encodeURIComponent(userEmail)}`, {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
      
          const responseText = await response.text();
          console.log("Raw Response Text:", responseText);
      
          if (response.ok) {
            // Fix malformed JSON string by replacing invalid parts
            const cleanedResponseText = responseText.replace(/\"paymentstatus\": \"([^\"]+)\"\"paymentmethod\"/g, '"paymentstatus": "$1", "paymentmethod"');
            
            // Now try to parse the cleaned response
            try {
              const data = JSON.parse(cleanedResponseText);
              setPastRides(data);
              console.log("Fetched ride details:", data);
            } catch (jsonError) {
              console.error("Error parsing JSON:", jsonError);
              console.error("Invalid JSON response:", cleanedResponseText);
            }
          } else {
            console.error("Failed to fetch past rides with status:", response.status);
          }
        } catch (error) {
          console.error("Error fetching past rides:", error);
        }
      };
      

    fetchRideDetails(); // Call the function when userEmail is available

    fetchPastRides();
    // Refresh every 5 seconds to get the updated ride details
    const interval = setInterval(fetchRideDetails, 15000);

    return () => clearInterval(interval); // Cleanup on unmount
  }, [userEmail]);

  const firstLetter = userEmail.charAt(0).toUpperCase();

  if (!rideDetails && pastRides.length === 0) {
    return <div>Loading...</div>; // Show loading text while waiting for the data
  }

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
                      {rideDetails.pickUpAddress}
                    </p>
                  </div>

                  {/* To */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      To
                    </p>
                    <h4 className="text-primary mb-1" style={{ fontSize: "1.5rem", fontWeight: "bold" }}>
                      {rideDetails.dropAddress}
                    </h4>
                  </div>

                  {/* Booked Time */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Booked Time
                    </p>
                    <p className="font-weight-bold mb-1" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      {rideDetails.createdAt}
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
                      {rideDetails.updatedAt}
                    </p>
                  </div>

                  {/* Vehicle Type (Cab Class) */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Vehicle
                    </p>
                    <h4 className="text-primary mb-1" style={{ fontSize: "1.5rem", fontWeight: "bold" }}>
                      {rideDetails.vehicleClass}
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

                {/* Driver Information */}
                <div className="mt-3">
                  {/* Driver Name */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                    Driver
                    </p>
                    <p className="font-weight-bold mb-1" style={{ fontSize: "1.1rem", fontWeight: "1000" }}>
                      {rideDetails.driverName}
                    </p>
                  </div>

                  {/* Contact No */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Contact
                    </p>
                    <p className="font-weight-bold mb-1" style={{ fontSize: "1.1rem", fontWeight: "1000" }}>
                      {rideDetails.driverTel}
                    </p>
                  </div>
                   {/* Vehicle No */}
                   <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Vehicle No
                    </p>
                    <h4 className="text-primary mb-1" style={{ fontSize: "1.2rem", fontWeight: "bold" }}>
                      {rideDetails.vehicleNumber}
                    </h4>
                  </div>

                </div>
              </div>

              {/* Footer */}
              <div className="card-footernoBg text-center">
                <p className="subtext text-muted" style={{ fontSize: "1rem", fontWeight: "500" }}>
                  Driver Details
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
                      {rideDetails.distance} km
                    </p>
                  </div>

                  {/* Estimated Fare */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Est. Fare
                    </p>
                    <p className="font-weight-bold mb-1" style={{ fontSize: "1rem", fontWeight: "1000" }}>
                      {rideDetails.fare} LKR
                    </p>
                  </div>

                  {/* Ride Status */}
                  <div className="d-flex align-items-center">
                    <p className="text-muted mb-1 me-2" style={{ fontSize: "1rem", fontWeight: "500" }}>
                      Status
                    </p>
                    <h4 className="text-success mb-1" style={{ fontSize: "1rem", fontWeight: "bold" }}>
                      {rideDetails.status}
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

        {/* Past Rides Table */}
{pastRides.length > 0 && (
    
  <div className="container mt-4">
    <div>
            <h1 className="admin-title">Past Ride</h1>
            <p className="admin-subtext">See all the information about current ride</p>
          </div>
    <div className="row">
      <div className="col-12">
        <div className="card full-height-card">
          <div className="table-responsive" style={{ maxHeight: "550px", overflowY: "auto" }}>
            <table className="table table-striped">
              <thead>
                <tr>
                  <th>Ride ID</th>
                  <th>Pickup Address</th>
                  <th>Drop Address</th>
                  <th>Booked Time</th>
                  <th>Updated Time</th>
                  <th>Vehicle Class</th>
                  <th>Vehicle Number</th>
                  <th>Driver Name</th>
                  <th>Driver Contact</th>
                  <th>Distance</th>
                  <th>Fare</th>
                  <th>Status</th>
                  <th>Payment Status</th>
                  <th>Payment Method</th>
                </tr>
              </thead>
              <tbody>
                {pastRides.map((ride) => (
                  <tr key={ride.rideId}>
                    <td>{ride.rideId}</td>
                    <td>{ride.pickUpAddress}</td>
                    <td>{ride.dropAddress}</td>
                    <td>{ride.createdAt}</td>
                    <td>{ride.updatedAt}</td>
                    <td>{ride.vehicleClass}</td>
                    <td>{ride.vehicleNumber}</td>
                    <td>{ride.driverName}</td>
                    <td>{ride.driverTel}</td>
                    <td>{ride.distance}</td>
                    <td>{ride.fare}</td>
                    <td>{ride.status}</td>
                    <td>{ride.paymentstatus}</td>
                    <td>{ride.paymentmethod}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
)}

      </div>
  );
};

export default PassengerMyRide;