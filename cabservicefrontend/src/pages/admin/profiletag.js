import React, { useState, useEffect, useRef } from "react"; 
import { useNavigate } from "react-router-dom";
import "../../css/profiletag.css";

const ProfileTag = ({ userEmail, accountType }) => {
  const navigate = useNavigate();
  const [isFlipped, setIsFlipped] = useState(false);
  const profileRef = useRef(null);

  const toggleMenu = () => {
    setIsFlipped((prev) => !prev);
  };

  const handleLogout = () => {
    const confirmLogout = window.confirm("Are you sure you want to logout?");
    if (confirmLogout) {
      localStorage.removeItem("authToken"); // Clear the JWT token
      navigate("/login"); // Redirect to login
    }
  };

  // Close menu if clicked outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        setIsFlipped(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  return (
    <div className="profile-container" ref={profileRef}>
      <div className={`profile-details ${isFlipped ? "flipped" : ""}`}>
        {/* Front Side (Profile Info) */}
        <div className="profile-front">
          <div className="profile-info">
            <h5 className="mb-0">{userEmail}</h5>
            <p className="account-type">{accountType}</p>
          </div>
          <div className="profile-arrow-column" onClick={toggleMenu}>
            <i className="bi bi-gear profile-arrow"></i>
          </div>
        </div>

        {/* Back Side (Menu) */}
        <div className="profile-back">
          <div className="menu-item" onClick={() => navigate("/profile")}>
            <i className="bi bi-person"></i> Profile
          </div>
          <div className="menu-item menu-item-logout" onClick={handleLogout}>
            <i className="bi bi-box-arrow-right logout-icon"></i> Logout
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfileTag;
