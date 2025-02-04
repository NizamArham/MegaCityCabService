import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import bigImage from "../images/image2.jpg"; // Import the image
import "../css/login.css"; // Ensure styles are imported
import "bootstrap-icons/font/bootstrap-icons.css";

const Login = () => {
  const [email, setEmail] = useState(""); // State for email
  const [password, setPassword] = useState(""); // State for password
  const [step, setStep] = useState(1); // Track the step (email or password)
  const navigate = useNavigate(); // For navigation

  const handleEmailSubmit = (e) => {
    e.preventDefault();
    setStep(2); // Move to password step
  };

  const handleLoginSubmit = (e) => {
    e.preventDefault();
    console.log("Email:", email);
    console.log("Password:", password);

    // Send credentials to the backend API using POST
    const loginData = {
      email: email,
      password: password,
    };

    fetch("http://localhost:8085/CabServiceBackend/login", {
      method: "POST", // POST method is being used here
      headers: {
        "Content-Type": "application/json", // Send data as JSON
      },
      body: JSON.stringify(loginData), // Convert login data to JSON string
    })
      .then((response) => response.json()) // Parse the JSON response
      .then((data) => {
        if (data.status === "success") {
          // Store the JWT token in localStorage
          localStorage.setItem("authToken", data.token); // Assuming 'data.token' contains the JWT token

          // Check account type and navigate accordingly
          if (data.acc_type === "customer") {
            navigate("/customerhome");
          } else if (data.acc_type === "driver") {
            navigate("/driverhome");
          } else if (data.acc_type === "admin") {
            navigate("/admin/home");
          } else {
            alert("Invalid account type.");
          }
        } else {
          alert("Invalid credentials. Please try again.");
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("An error occurred. Please try again.");
      });
  };

  return (
    <div className="login-page-container">
      {/* Left Side - Image */}
      <div className="login-image">
        <img src={bigImage} alt="Login" />

        {/* Back Button */}
        <button className="back-btn" onClick={() => navigate("/")}>
          {/* Back Arrow SVG */}
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
            <path d="M14 19l-7-7 7-7" stroke="white" strokeWidth="2" fill="none" />
          </svg>
        </button>
      </div>

      {/* Right Side - Login Form */}
      <div className="login-form">
        <h3>Welcome!</h3>
        <p>Enter your email to get started.</p>

        <form onSubmit={step === 1 ? handleEmailSubmit : handleLoginSubmit}>
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
                disabled={step === 2} // Disable email input after step 1
              />
            </div>
            {step === 2 && (
              <div className="input-group mt-3">
                <span className="input-group-text">
                  <i className="bi bi-key"></i>
                </span>
                <input
                  type="password"
                  className="form-control"
                  placeholder="Enter your password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
            )}
            {/* Centered Submit Button */}
            <div className="btn-container">
              <button type="submit" className="btn btn-submit w-100">
                {step === 1 ? "Continue with Email" : "Continue to Login"}
              </button>
            </div>
          </div>
        </form>

        {/* Small Text and Sign Up Link at Step 1 */}
        {step === 1 && (
          <div className="text-center mt-3">
            <p className="small-text" style={{ fontSize: '15px', color: '#666' }}>
              Don't have an account?{" "}
              <a href="/signin" className="text-decoration-none">
                Sign up
              </a>
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Login;
