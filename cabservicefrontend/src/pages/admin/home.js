import React, { useEffect, useState, useRef } from "react";
import { jwtDecode } from "jwt-decode";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import "../../css/adminhome.css";
import { useNavigate } from "react-router-dom";
import ProfileTag from "../profiletag";

import axios from "axios";

const AdminHome = ({ accountType = "Admin" }) => {
  const [userEmail, setUserEmail] = useState("");
  const [sessionExpired, setSessionExpired] = useState(false);
  const [cabCount, setCabCount] = useState(0);
  const [driverCount, setDriverCount] = useState(0);
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

    const fetchCounts = async () => {
      try {
        const response = await axios.get("http://localhost:8080/CabService/adminoverview");
        console.log("Response Data:", response.data);
        setCabCount(response.data.cabCount);
        setDriverCount(response.data.driverCount);
      } catch (error) {
        console.error("Error fetching counts", error);
      }
    };

    fetchCounts();

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

  // Handler for Add Destinations Button
  const handleAddDestinationsClick = () => {
    navigate("/admin/route"); // Navigate to the route page
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
            <h1 className="admin-title">Admin Portal</h1>
            <p className="admin-subtext">See all the information on this page</p>
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
          {/* Large Card */}
          <div className="col-md-6 mb-4">
            <div className="card large-card">
              <div className="card-body">
                <div className="card-icons">
                  <div className="icon-left">
                    <i className="bi bi-wallet"></i>
                  </div>
                  <div className="icon-right">
                    <i className="bi bi-arrow-right"></i>
                  </div>
                </div>
                <div className="card-footer">
                  <h6 className="large-text">LKR 323,002.23</h6>
                  <p className="subtext">In this month</p>
                </div>
              </div>
            </div>
          </div>

          {/* Small Cards */}
          <div className="col-md-3 mb-4">
            <div className="card small-card">
              <div className="card-body">
                <div className="card-icons">
                  <div className="icon-left">
                    <i className="bi bi-truck"></i>
                  </div>
                  <div className="icon-right" onClick={() => navigate("/admin/cab")} style={{ cursor: "pointer" }}>
                    <i className="bi bi-arrow-right"></i>
                  </div>
                </div>
                <div className="card-footer">
                  <h6 className="large-text">{cabCount}</h6>
                  <p className="subtext">Total Cabs</p>
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-3 mb-4">
            <div className="card small-card">
              <div className="card-body">
                <div className="card-icons">
                  <div className="icon-left">
                    <i className="bi bi-people"></i>
                  </div>
                  <div className="icon-right" onClick={() => navigate("/admin/drivers")} style={{ cursor: "pointer" }}>
                    <i className="bi bi-arrow-right"></i>
                  </div>
                </div>
                <div className="card-footer">
                  <h6 className="large-text">{driverCount}</h6>
                  <p className="subtext">Total Drivers</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Second Row - Same Layout */}
        <div className="row">
          <div className="col-md-4 mb-4">
            <div className="card small-card big-card map-card">
              <div className="card-body">
                <div className="card-icons">
                  <div className="icon-left">
                    <i className="bi bi-map"></i>
                  </div>
                  <div className="icon-right" style={{ cursor: "pointer" }}>
                    <i className="bi bi-arrow-right"></i>
                  </div>
                </div>
                <div className="button-container">
                  <button className="action-btn" onClick={handleAddDestinationsClick}>
                    Add Destinations
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-3 mb-4">
            <div className="card small-card big-card ongoing-card">
              <div className="card-body">
                <div className="card-icons">
                  <div className="icon-right" style={{ cursor: "pointer" }}>
                    <i className="bi bi-arrow-right"></i>
                  </div>
                </div>
                <div className="button-container">
                  <button className="action-btn">
                    On Going Rides
                  </button>
                </div>
              </div>
            </div>
          </div>

          {/* Large Card */}
<div className="col-md-5 mb-4">
  <div className="card large-card big-card">
    <div className="card-body">
      <div className="card-icons">
        <div className="icon-left">
          <i className="bi bi-bar-chart"></i>
        </div>
        <div className="icon-right">
          <i className="bi bi-arrow-right"></i>
        </div>
      </div>
      <div className="card-footer">
        <h6 className="large-text">LKR 1,350,450.09</h6>
        <p className="subtext">Total Revenue</p>
        
        {/* Small Analytics Field */}
        <div className="analytics-field mt-3">
          <div className="d-flex justify-content-between align-items-center">
            <span className="analytics-text">+12%</span>
            <span className="analytics-subtext">vs last month</span>
          </div>
          <div className="progress mt-2">
            <div
              className="progress-bar"
              role="progressbar"
              style={{ width: "65%" }}
              aria-valuenow="65"
              aria-valuemin="0"
              aria-valuemax="100"
            ></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
        </div>
      </div>
    </div>
  );
};

export default AdminHome;