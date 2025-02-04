import React, { useEffect, useState, useRef } from "react";
import { jwtDecode } from "jwt-decode";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import "../../css/adminhome.css";
import { useNavigate } from "react-router-dom";
import ProfileTag from "./profiletag";

const AdminHome = ({ accountType = "Admin" }) => {
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
            <h1 className="admin-title">Admin Portal</h1>
            <p className="admin-subtext">See all the information on this page</p>
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
          {/* First Large Card */}
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
                  <h6 className="large-text">LKR 1,000,000</h6>
                  <p className="subtext">In this month</p>
                </div>
              </div>
            </div>
          </div>

          {/* Second Small Card */}
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
                  <h6 className="large-text">80</h6>
                  <p className="subtext">Total Cabs</p>
                </div>
              </div>
            </div>
          </div>

          {/* Third Small Card */}
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
                  <h6 className="large-text">80</h6>
                  <p className="subtext">Total Drivers</p>
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
