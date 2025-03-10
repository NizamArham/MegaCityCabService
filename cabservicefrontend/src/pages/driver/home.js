import React, { useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import { useNavigate } from "react-router-dom";
import { Modal, Button, Form } from 'react-bootstrap';
import ProfileTag from "../profiletag";
import blacklogoimage from '../../images/MegaCityLogo.png';
import "../../css/driverhome.css";

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
                                <a className="nav-link nav-box" href="/driver/myride">Rides</a>
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

const DriverHome = ({ accountType = "Driver" }) => {
    const [userEmail, setUserEmail] = useState("");
    const [sessionExpired, setSessionExpired] = useState(false);
    const [showLocationModal, setShowLocationModal] = useState(false);
    const [selectedLocation, setSelectedLocation] = useState("");
    const [currentLocation, setCurrentLocation] = useState("");
    const [locations, setLocations] = useState([]);  // Store fetched locations
    const [rides, setRides] = useState([]);
    const [noRides, setNoRides] = useState(false);  // State to track if no rides are available
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
            if (decodedToken.acc_type) accountType = decodedToken.acc_type;
        } catch (error) {
            console.error("Invalid token", error);
        }
    }, [navigate]);

    useEffect(() => {
        if (userEmail && !sessionExpired) setShowLocationModal(true);
    }, [userEmail, sessionExpired]);

    // Fetch locations from the backend
    useEffect(() => {
        fetch("http://localhost:8080/CabService/fetchlocations")
            .then(response => response.json())
            .then(data => {
                // Remove duplicates, sort alphabetically
                const uniqueSortedLocations = [...new Set(data)].sort();
                setLocations(uniqueSortedLocations);
            })
            .catch(error => console.error("Error fetching locations:", error));
    }, []);

    const refreshRides = () => {
        fetch("http://localhost:8080/CabService/CurrentLocationRides", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ userEmail, location: currentLocation })
        })
        .then(response => response.json())
        .then(data => {
            if (data.length === 0) {
                setNoRides(true);
                setRides([]);
            } else {
                setRides(data);
                setNoRides(false);
            }
        })
        .catch(error => console.error("Error fetching rides:", error));
    };
    

    useEffect(() => {
        if (currentLocation) {
            fetch("http://localhost:8080/CabService/CurrentLocationRides", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ userEmail, location: currentLocation })
            })
            .then(response => response.json())
            .then(data => {
                if (data.length === 0) {
                    setNoRides(true);
                    setRides([]);
                } else {
                    setRides(data);
                    setNoRides(false);
                }
            })
            .catch(error => console.error("Error fetching rides:", error));
        }
    }, [currentLocation]);  // Runs whenever currentLocation changes
    

    const handleLocationSubmit = () => {
        if (selectedLocation) {
            setCurrentLocation(selectedLocation);
            setShowLocationModal(false);

            console.log("Selected location:", selectedLocation , userEmail);

            // Send selected location to backend
            fetch("http://localhost:8080/CabService/CurrentLocationRides", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ userEmail, location: selectedLocation })
            })
            .then(response => response.json())
            .then(data => {
                if (data.length === 0) {
                    setNoRides(true);
                } else {
                    setRides(data);
                    setNoRides(false);
                }
            })
            .catch(error => console.error("Error fetching rides:", error));
        }
    };

    const handleLocationDisplayClick = () => {
        setSelectedLocation(currentLocation);
        setShowLocationModal(true);
    };
    const handleAcceptRide = (vehicleNumber, bookingId) => {
        console.log("Attempting to accept ride with vehicle:", vehicleNumber, "and booking ID:", bookingId);
      
        fetch("http://localhost:8080/CabService/acceptRide", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            vehicle: vehicleNumber,
            driverEmail: userEmail, // Ensure userEmail is defined in your component
            bookingId: bookingId,
          }),
          credentials: "include", // Include credentials if necessary
        })
          .then((response) => {
            if (!response.ok) {
              throw new Error("Failed to accept ride: " + response.statusText);
            }
            return response.json();
          })
          .then((data) => {
            if (data.status === "success") {
              console.log("Ride accepted:", data);
              alert("Ride accepted successfully!");
      
              // Refresh the list of rides
              refreshRides(); // Ensure refreshRides is defined
            } else if (data.status === "already_in_ride") {
              // Handle the case where the rider is already in a ride
              console.log("Rider already in a ride:", data.message);
              alert(data.message || "You are already on a ride.");
            } else {
              // Handle other server-side errors
              console.error("Server error:", data.message);
              alert(data.message || "An error occurred while accepting the ride.");
            }
          })
          .catch((error) => {
            console.error("Network or other error:", error);
            alert("An error occurred: " + error.message);
          });
      };

    return (
        <div className="admin-container">
            <Header userEmail={userEmail} accountType={accountType} />

            {sessionExpired && (
                <div className="session-expired-message">
                    <p>Your session has expired. Please log in again.</p>
                </div>
            )}

            <div className="container custom-margin" style={{ marginTop: "1000px" }}>
                {selectedLocation && (
                    <div className="row">
                        {/* Loop through rides */}
                        {rides.length > 0 ? (
                            rides.map((ride, index) => (
                                <div className="col-md-3 mb-4" key={index}>
                                    <div className="card ride-details-card shadow-sm">
                                        <div className="card-body">
                                            <div className="ride-header">
                                                {/* Title and Location */}
                                                <div className="ride-location">
                                                    {/* "To" and Destination */}
                                                    <div className="d-flex align-items-center mb-0">
                                                        <p className="text-muted" style={{ fontSize: "1rem", marginRight: "5px", fontWeight: "500" }}>To</p>
                                                        <h4 className="text-primary" style={{ fontSize: "2rem", fontWeight: "bold" }}>{ride.destination}</h4> {/* Use ride.destination */}
                                                    </div>
                                                    <hr />

                                                    {/* "From" and Starting Location */}
                                                    <div className="d-flex align-items-center mb-0">
                                                        <p className="text-muted" style={{ fontSize: "1rem", marginRight: "5px", fontWeight: "500" }}>From</p>
                                                        <p className="font-weight-bold" style={{ fontSize: "1.1rem",fontWeight: "1000" }}>{ride.pickupLocation}</p> {/* Use ride.pickupLocation */}
                                                    </div>
                                                </div>
                                            </div>
                                            {/* Ride Details */}
                                            <div className="ride-details">
                                                {/* Passenger Info */}
                                                <p className="mb-1" style={{ fontSize: "1rem" }}><i className="bi bi-person" style={{ marginRight: "5px" }}></i> {ride.passengerName}</p> {/* Use ride.passengerName */}
                                                <p className="mb-1" style={{ fontSize: "1rem" }}><i className="bi bi-telephone" style={{ marginRight: "5px" }}></i> {ride.passengerContact}</p> {/* Use ride.passengerContact */}
                                                <p className="mb-1" style={{ fontSize: "1rem" }}><i className="bi bi-envelope" style={{ marginRight: "5px" }}></i> {ride.passengerEmail}</p> {/* Use ride.passengerEmail */}
                                            </div>

                                            {/* Button Section */}
                                            <div className="mt-3 d-flex justify-content-center">
                                                <button className="btn btn-success" style={{ fontSize: "1rem", padding: "10px 20px", width: "100%" }}
                                                 onClick={() => handleAcceptRide(ride.vehicle , ride.id)}>
                                                    Accept
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ))
                        ) : (
                            <h4>No rides available for this location at the moment.</h4>  // Display message if no rides are available
                        )}
                    </div>
                )}

                <Modal
                    show={showLocationModal}
                    onHide={() => setShowLocationModal(false)}
                    centered
                    backdrop="static"
                    keyboard={false}
                >
                    <Modal.Header>
                        <Modal.Title> Select Your Location</Modal.Title>
                    </Modal.Header>
                    
                    <Modal.Body>
                        <Form>
                            <Form.Group controlId="locationSelect">
                                <Form.Label>Choose your current location</Form.Label>
                                <Form.Control
                                    as="select"
                                    value={selectedLocation}
                                    onChange={(e) => setSelectedLocation(e.target.value)}
                                    required
                                >
                                    <option value="">Choose a location</option>
                                    {locations.map((loc, index) => (
                                        <option key={index} value={loc}>{loc}</option>
                                    ))}
                                </Form.Control>
                            </Form.Group>
                        </Form>
                    </Modal.Body>

                    <Modal.Footer>
                        <Button
                            variant="primary"
                            onClick={handleLocationSubmit}
                            disabled={!selectedLocation}
                        >
                            Set Location
                        </Button>
                    </Modal.Footer>
                </Modal>

                {currentLocation && (
                    <div 
                        className="current-location-display"
                        onClick={handleLocationDisplayClick}
                    >
                        <i className="bi bi-globe"></i>
                        <span>{currentLocation}</span>
                    </div>
                )}
            </div>
        </div>
    );
};

export default DriverHome;
