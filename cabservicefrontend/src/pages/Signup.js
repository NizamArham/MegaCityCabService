import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import bigImage from "../images/image2.jpg"; // Import the image
import "../css/signup.css"; // Ensure styles are imported
import "bootstrap-icons/font/bootstrap-icons.css"; // Bootstrap Icons

const SignupPage = () => {
  const [firstName, setFirstName] = useState(""); 
  const [lastName, setLastName] = useState(""); 
  const [nic, setNic] = useState(""); 
  const [tp, setTp] = useState(""); 
  const [email, setEmail] = useState(""); 
  const [password, setPassword] = useState(""); 
  const [confirmPassword, setConfirmPassword] = useState(""); 
  const [passwordError, setPasswordError] = useState(false); // Track password mismatch
  const navigate = useNavigate();

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
    setPasswordError(false); // Reset error when typing
  };

  const handleConfirmPasswordChange = (e) => {
    setConfirmPassword(e.target.value);
    setPasswordError(false); // Reset error when typing
  };

  const handleSignupSubmit = (e) => {
    e.preventDefault();

    // Check if passwords match
    if (password !== confirmPassword) {
      setPasswordError(true);
      setConfirmPassword(""); // Clear the confirm password field
      return;
    }

    const signupData = {
      firstName,
      lastName,
      nic,
      tp,
      email,
      password,
    };

    fetch("http://localhost:8080/CabServiceBackend/signup", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(signupData),
    })
      .then((response) => response.json()) 
      .then((data) => {
        console.log("Response from backend:", data); 
    
        if (data.status === "success") {
          alert("Sign up successful! Welcome to the MegaCityCab family!");
          navigate("/login");
        } else {
          alert(data.message);
        }
      })
      .catch((error) => {
        console.error("Error occurred:", error);
        alert("An error occurred. Please try again.");
      });
  };

  return (
    <div className="signup-page-container">
      <div className="signup-image">
        <img src={bigImage} alt="Signup" />
        <button className="back-btn" onClick={() => navigate("/")}>
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
            <path d="M14 19l-7-7 7-7" stroke="white" strokeWidth="2" fill="none" />
          </svg>
        </button>
      </div>

      <div className="signup-form">
        <h3>Create an Account</h3>
        <p>Please provide your details to sign up.</p>
        
        <form onSubmit={handleSignupSubmit}>
                      {/* Step 1: First Name, Last Name, NIC, and Telephone */}
          <div className="mb-3">
            <div className="input-group">
              <span className="input-group-text">
              <i className="bi bi-person-badge"></i>
              </span>
              <input
                type="text"
                className="form-control"
                placeholder="First Name"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                required
              />
            </div>
          </div>
          <div className="mb-3">
            <div className="input-group">
              <span className="input-group-text">
                <i className="bi bi-person"></i>
              </span>
              <input
                type="text"
                className="form-control"
                placeholder="Last Name"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                required
              />
            </div>
          </div>
          {/* NIC */}
          <div className="mb-3">
            <div className="input-group">
              <span className="input-group-text">
                <i className="bi bi-card-text"></i>
              </span>
              <input
                type="text"
                className="form-control"
                placeholder="NIC"
                value={nic}
                onChange={(e) => {
                  const newValue = e.target.value;

                  // Allow only numbers and max length of 12 characters
                  if (/^\d{0,9}[vx]?$/.test(newValue) || /^\d{0,12}$/.test(newValue)) {
                    setNic(newValue);
                  }
                }}
                required
              />
            </div>
          </div>
          {/* Telephone */}
          <div className="mb-3">
            <div className="input-group">
              <span className="input-group-text">
                <i className="bi bi-telephone"></i>
              </span>
              <input
                type="tel"
                className="form-control"
                placeholder="Telephone"
                value={tp}
                onChange={(e) => setTp(e.target.value)}
                required
              />
            </div>
          </div>

          {/* Step 2: Email */}
          <div className="mb-3">
            <div className="input-group">
              <span className="input-group-text">
                <i className="bi bi-envelope"></i>
              </span>
              <input
                type="email"
                className="form-control"
                placeholder="Enter your email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
          </div>

          {/* Step 3: Password and Confirm Password */}
          <div className="mb-3">
            <div className="input-group">
              <span className="input-group-text">
                <i className="bi bi-key"></i>
              </span>
              <input
                type="password"
                className={`form-control ${passwordError ? "error-outline" : ""}`}
                placeholder="Enter your password"
                value={password}
                onChange={handlePasswordChange}
                required
              />
            </div>
          </div>
          <div className="mb-3">
            <div className="input-group">
              <span className="input-group-text">
                <i className="bi bi-shield-lock"></i>
              </span>
              <input
                type="password"
                className={`form-control ${passwordError ? "error-outline" : ""}`}
                placeholder="Confirm your password"
                value={confirmPassword}
                onChange={handleConfirmPasswordChange}
                required
              />
            </div>
          </div>

          {passwordError && (
            <p style={{ color: "red", fontSize: "14px", marginBottom: "10px" }}>
              Passwords do not match. Please try again.
            </p>
          )}

          <div className="terms-text">
            <p style={{ fontSize: "15px", color: "#666" }}>
              By creating an account, you agree to our<br />
              <a href="/terms">Terms of Service</a> and <a href="/privacy">Privacy & Cookie Statement</a>.
            </p>
          </div>

          <div className="btn-container">
            <button type="submit" className="btn btn-submit w-100" disabled={passwordError}>
              Create Account
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default SignupPage;
