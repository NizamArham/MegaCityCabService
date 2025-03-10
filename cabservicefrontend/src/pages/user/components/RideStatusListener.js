import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const RideStatusListener = ({ bookingId }) => {
  const [status, setStatus] = useState("Requested");
  const navigate = useNavigate();

  useEffect(() => {
    if (!bookingId) return;
    console.log("Booking ID at listner:", bookingId);

    // Function to check booking status
    const checkBookingStatus = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/CabService/bookingstatus?bookingID=${bookingId}`
        );
        if (response.ok) {
          const data = await response.json();
          setStatus(data.rideStatus);

          // Debugging: Log the status
          console.log("Current Status:", data.status);

          // If the ride is accepted, redirect to /user/myride
          if (data.rideStatus === "occupied") {
            console.log("Ride accepted. Redirecting to /user/myride...");
            navigate("/user/myride");
          }
        } else {
          console.error("Failed to fetch booking status");
        }
      } catch (error) {
        console.error("Error checking booking status:", error);
      }
    };

    const intervalId = setInterval(checkBookingStatus, 5000);

    // Cleanup interval on component unmount
    return () => clearInterval(intervalId);
  }, [bookingId, navigate]);

  return (
    <div>
      <p>Ride Status: {status}</p>
    </div>
  );
};

export default RideStatusListener;