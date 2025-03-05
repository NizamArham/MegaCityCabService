import React, { useEffect, useState, useRef } from "react"; // Add useRef here
import { jwtDecode } from "jwt-decode";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import { useNavigate } from "react-router-dom";
import ProfileTag from "../profiletag";
import "../../css/userhome.css";
import blacklogoimage from '../../images/MegaCityLogowhite.png';

const images = Array.from({ length: 9 }, (_, i) => require(`../../images/image${i + 1}.jpg`));

const Header = ({ userEmail, accountType }) => {
  const firstLetter = userEmail.charAt(0).toUpperCase();
  return (
    <nav className="landingpage-navbar navbar navbar-expand-lg navbar-light">
      <div className="container">
        <a className="navbar-brand" href="#">
          <img src={blacklogoimage} alt="Mega City Cab Logo" className="logo-image" />
        </a>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ml-auto">
            <div className="nav-group">
              <li className="nav-item">
                <a className="nav-link nav-box" href="#howItWorks">Rides</a>
              </li>
              <li className="nav-item">
                <a className="nav-link nav-box" href="#contactUs">Contacts</a>
              </li>
            </div>
            <div className="admin-profile d-flex align-items-center">
              <ProfileTag userEmail={userEmail} accountType={accountType} />
              <div className="profile-icon">{firstLetter}</div>
            </div>
          </ul>
        </div>
      </div>
    </nav>
  );
};

const LoadingAnimation = () => {
  return (
    <div className="loading-container">
      <div className="dotted-loader">
        <div className="dot"></div>
        <div className="dot"></div>
        <div className="dot"></div>
      </div>
      <div className="loading-text">Searching for a best match...</div>
    </div>
  );
};

const DropCard = ({userEmail}) => {
  const [dropAddress, setDropAddress] = useState("");
  const [pickUpAddress, setPickUpAddress] = useState("");  
  const [isExpanded, setIsExpanded] = useState(false); 
  const [selectedCabClass, setSelectedCabClass] = useState(null);
  const [selectedVehicleType, setSelectedVehicleType] = useState(null);
  const [pickUpSuggestions, setPickUpSuggestions] = useState([]);  // Separate state for pickup suggestions
  const [dropSuggestions, setDropSuggestions] = useState([]);    // Separate state for drop suggestions
  const [noMatch, setNoMatch] = useState(false); // Track if no match found

  // Fetch suggestions from the backend API
  const fetchSuggestions = async (input, type) => {
    if (input.length > 2) { // Trigger suggestions when the input length is greater than 2
      const response = await fetch(`http://localhost:8080/CabService/locations?query=${input}`);
      const data = await response.json();
      
      if (type === "pickUp") {
        setPickUpSuggestions(data); // Update pickup suggestions
      } else {
        setDropSuggestions(data); // Update drop suggestions
      }

      // Check if no matches found
      if (data.length === 0) {
        setNoMatch(true); // Set red border if no match found
      } else {
        setNoMatch(false); // Reset to normal if matches found
      }
    } else {
      if (type === "pickUp") {
        setPickUpSuggestions([]);
      } else {
        setDropSuggestions([]);
      }
      setNoMatch(false); // Reset to normal if input length is too short
    }
  };

  const pickUpInputRef = useRef(null); // Define useRef for pickup input
  const dropInputRef = useRef(null); // Define useRef for drop input

  const handlePickUpAddressChange = (e) => {
    const input = e.target.value;
    setPickUpAddress(input);
    fetchSuggestions(input, "pickUp");

    const matchingSuggestion = pickUpSuggestions.find(suggestion => 
      suggestion.toLowerCase().startsWith(input.toLowerCase())
    );

    if (matchingSuggestion && input.length > 0) {
      const remainingText = matchingSuggestion.substring(input.length);
      e.target.placeholder = `${input}${remainingText}`;
    } else {
      e.target.placeholder = "I'm at...";
    }
  };

  const handleDropAddressChange = (e) => {
    const input = e.target.value;
    setDropAddress(input);
    fetchSuggestions(input, "drop");

    const matchingSuggestion = dropSuggestions.find(suggestion => 
      suggestion.toLowerCase().startsWith(input.toLowerCase())
    );

    if (matchingSuggestion && input.length > 0) {
      const remainingText = matchingSuggestion.substring(input.length);
      e.target.placeholder = `${input}${remainingText}`;
    } else {
      e.target.placeholder = "I'm going to...";
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Tab") {
      e.preventDefault();
      const input = e.target.value;
      const suggestions = e.target === pickUpInputRef.current ? pickUpSuggestions : dropSuggestions;
      const matchingSuggestion = suggestions.find(suggestion => 
        suggestion.toLowerCase().startsWith(input.toLowerCase())
      );

      if (matchingSuggestion) {
        if (e.target === pickUpInputRef.current) {
          setPickUpAddress(matchingSuggestion);
        } else {
          setDropAddress(matchingSuggestion);
        }
      }
    } else if (e.key === "Enter" && dropAddress.trim() !== "" && pickUpAddress.trim() !== "") {
      setIsExpanded(true);
    }
  };

  const handleCabClassSelection = (cls) => {
    setSelectedCabClass(cls);
    setSelectedVehicleType(null); // Reset vehicle selection when changing cab class
  };

  const handleVehicleTypeSelection = async (type) => {
    setSelectedVehicleType(type);
  
    const bookingData = {
      pickUpAddress,
      dropAddress,
      cabClass: selectedCabClass,
      vehicleType: type,
      bookingStatus: 'Pending',
      passengerEmail: userEmail,
    };
    console.log("Booking Data sent to Backend:", bookingData); // Log the booking data to check its values

    try {
      const response = await fetch("http://localhost:8080/CabService/createbooking", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(bookingData),
      });
  
      if (response.ok) {
        console.log("Booking sent Successfully!");
      } else {
        console.error("Booking failed.");
      }
    } catch (error) {
      console.error("Error sending booking request:", error);
    }
  
    setTimeout(() => {
      setIsExpanded(false); 
    }, 500);
  };

  
  const cabClasses = ["Economy", "Standard", "Semi-Luxury", "Luxury"];
  const vehicleTypes = ["Sedan", "SUV", "Hatchback", "Minivan"];

  // Function to get filtered suggestions based on current input
  const handleSuggestionClick = (suggestion, type) => {
    if (type === "pickUp") {
      setPickUpAddress(suggestion); // Set the selected pickup suggestion
    } else {
      setDropAddress(suggestion); // Set the selected drop suggestion
    }
    if (type === "pickUp") {
      setPickUpSuggestions([]); // Clear pickup suggestions after selection
    } else {
      setDropSuggestions([]); // Clear drop suggestions after selection
    }
  };

  return (
    <div className={`drop-card ${isExpanded ? 'expanded' : ''}`}>
      <div className="drop-input-container">
        <div className="drop-inputs-container">
          <div className="drop-field">
            <h5 className="drop-title bold-text">Pick Up</h5>
            <div className="drop-input">
              <input
                ref={pickUpInputRef}
                type="text"
                placeholder="I'm at..."
                className={`drop-text ${noMatch && pickUpAddress.length > 2 ? 'input-error' : ''}`}
                value={pickUpAddress}
                onChange={handlePickUpAddressChange}
                onKeyDown={handleKeyDown}
                disabled={isExpanded}
              />
              <i className="bi bi-house-door drop-icon"></i>
              {/* Show suggestions inside the input */}
              {pickUpAddress && pickUpSuggestions.length > 0 && (
                <div className="suggestions">
                  {pickUpSuggestions.map((suggestion, index) => (
                    <div 
                      key={index} 
                      className="suggestion-item" 
                      onClick={() => handleSuggestionClick(suggestion, "pickUp")}
                    >
                      {suggestion}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          <div className="separator">•-------------------•</div>

          <div className="drop-field">
            <h5 className="drop-title bold-text">Drop</h5>
            <div className="drop-input">
              <input
                ref={dropInputRef}
                type="text"
                placeholder="I'm going to..."
                className={`drop-text ${noMatch && dropAddress.length > 2 ? 'input-error' : ''}`}
                value={dropAddress}
                onChange={handleDropAddressChange}
                onKeyDown={handleKeyDown}
                disabled={isExpanded}
              />
              <i className="bi bi-flag drop-icon"></i>
              {/* Show suggestions inside the input */}
              {dropAddress && dropSuggestions.length > 0 && (
                <div className="suggestions">
                  {dropSuggestions.map((suggestion, index) => (
                    <div 
                      key={index} 
                      className="suggestion-item" 
                      onClick={() => handleSuggestionClick(suggestion, "drop")}
                    >
                      {suggestion}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Step 1: Select Cab Class - Stays Visible After Selection */}
      {isExpanded && !selectedVehicleType && (
        <div className={`cab-class-selection ${selectedCabClass ? 'locked' : ''}`}>
          <h5 className="bold-text">Select Cab Class</h5>
          <div className="cab-class-tags">
            {cabClasses.map((cls, index) => (
              <button
                key={index}
                className={`cab-class-btn ${selectedCabClass === cls ? 'selected' : ''}`}
                onClick={() => handleCabClassSelection(cls)}
              >
                {cls}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Step 2: Select Vehicle Type - Expands After Cab Class is Selected */}
      {selectedCabClass && !selectedVehicleType && (
        <div className="vehicle-type-selection expanded">
          <h5 className="bold-text">Select Vehicle Type</h5>
          <div className="vehicle-type-tags">
            {vehicleTypes.map((type, index) => (
              <div
                key={index}
                className={`vehicle-type-card ${selectedVehicleType === type ? 'selected' : ''}`}
                onClick={() => handleVehicleTypeSelection(type)}
              >
                {type}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Step 4: Loading Animation */}
      {selectedCabClass && selectedVehicleType && (
        <LoadingAnimation />
      )}
    </div>
  );
};

const UserHome = ({ accountType = "User" }) => {
  const [userEmail, setUserEmail] = useState("");
  const [sessionExpired, setSessionExpired] = useState(false);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("authToken");
    if (!token) {
      setSessionExpired(true);
      setTimeout(() => navigate("/login"), 3000);
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
    const interval = setInterval(() => {
      setCurrentImageIndex((prevIndex) => (prevIndex + 1) % images.length);
    }, 10000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="admin-container" style={{
      backgroundImage: `url(${images[currentImageIndex]})`,
      backgroundSize: "cover",
      backgroundPosition: "center",
      transition: "background-image 1s ease-in-out"
    }}>
      <Header userEmail={userEmail} accountType={accountType} />
      <DropCard userEmail={userEmail} />
      {sessionExpired && (
        <div className="session-expired-message">
          <p>Your session has expired. Please log in again.</p>
        </div>
      )}
    </div>
  );
};

export default UserHome;