import React, { useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import { useNavigate } from "react-router-dom";
import { Modal, Button, Form } from 'react-bootstrap';
import ProfileTag from "../profiletag";
import blacklogoimage from '../../images/MegaCityLogowhite.png';
import "../../css/driverhome.css";

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

const DriverHome = ({ accountType = "Driver" }) => {
    const [userEmail, setUserEmail] = useState("");
    const [sessionExpired, setSessionExpired] = useState(false);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [showLocationModal, setShowLocationModal] = useState(false);
    const [selectedLocation, setSelectedLocation] = useState("");
    const [currentLocation, setCurrentLocation] = useState("");
    const [locations, setLocations] = useState([]);  // Store fetched locations
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
        const interval = setInterval(() => {
            setCurrentImageIndex(prev => (prev + 1) % images.length);
        }, 10000);
        return () => clearInterval(interval);
    }, []);

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

    const handleLocationSubmit = () => {
        if (selectedLocation) {
            setCurrentLocation(selectedLocation);
            setShowLocationModal(false);

            console.log("Selected location:", selectedLocation);
    
            // Send selected location to backend
            fetch("http://localhost:8080/CabService/fetchlocations", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ userEmail, location: selectedLocation })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Failed to update location");
                }
                return response.json();
            })
            .then(data => console.log("Location updated successfully:", data))
            .catch(error => console.error("Error updating location:", error));
        }
    };
    

    const handleLocationDisplayClick = () => {
        setSelectedLocation(currentLocation);
        setShowLocationModal(true);
    };

    return (
        <div className="admin-container" style={{
            backgroundImage: `url(${images[currentImageIndex]})`,
            backgroundSize: "cover",
            backgroundPosition: "center",
            transition: "background-image 1s ease-in-out"
        }}>
            <Header userEmail={userEmail} accountType={accountType} />

            {sessionExpired && (
                <div className="session-expired-message">
                    <p>Your session has expired. Please log in again.</p>
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
    );
};

export default DriverHome;
