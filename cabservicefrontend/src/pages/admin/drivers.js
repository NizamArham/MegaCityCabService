import React, { useEffect, useState, useRef } from "react";
import { jwtDecode } from "jwt-decode";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import "../../css/cab.css";
import { useNavigate } from "react-router-dom";
import ProfileTag from "./profiletag";

const AdminDrivers = ({ accountType = "Admin" }) => {
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

    // Detect clicks outside the profile menu to reset the flip state
    const handleClickOutside = (event) => {
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        // Set flip state to false here if necessary
      }
    };

    // Add event listener on mount
    document.addEventListener("mousedown", handleClickOutside);

    // Cleanup event listener on unmount
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [navigate]);

  const firstLetter = userEmail.charAt(0).toUpperCase();

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
            <h1 className="admin-title">Manage Drivers</h1>
            <p className="admin-subtext">View and Manage All the Drivers here </p>
          </div>
          <div className="admin-profile d-flex align-items-center" ref={profileRef}>
            {/* First letter is outside of the flipping component */}
            <div className="profile-icon">{firstLetter}</div> 

            <ProfileTag
              userEmail={userEmail}
              accountType={accountType}
            />
          </div>
        </header>
      )}

      <div className="container mt-4">
        <div className="row">
          {/* Full-Height Large Card */}
          <div className="col-12">
            <div className="card full-height-card">
              <div className="card-body d-flex flex-column justify-content-between">
                {/* will add the all the Drivers here in a table with rows and colomns*/}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="floating-btn-container">
        <p className="floating-btn-text">New Driver</p>
        <button className="floating-btn">
          <i className="bi bi-plus"></i>
        </button>
      </div>

   </div>
  );
};

export default AdminDrivers;
