import React, { useEffect, useState, useRef } from "react";
import { jwtDecode } from "jwt-decode";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import ProfileTag from "../profiletag.js";
import AddCabModal from "../admin/components/addcarmodel.js";
import EditCabModal from "../admin/components/editcabmodal.js"; 
import "../../css/cab.css";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const AdminCab = () => {
  const [userEmail, setUserEmail] = useState("");
  const [sessionExpired, setSessionExpired] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [vehicles, setVehicles] = useState([]);
  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const accountType = useRef("Admin"); 
  const navigate = useNavigate();
  const profileRef = useRef(null);

  const handleShow = () => setShowModal(true);
  const handleClose = () => setShowModal(false);

  const handleShowEditModal = (vehicle) => {
    setSelectedVehicle(vehicle);
    setShowEditModal(true);
  };

  const handleCloseEditModal = () => setShowEditModal(false);
  
  const handleDelete = async (vehicleId) => {
    console.log("Attempting to delete vehicle with ID:", vehicleId);
    
    if (!vehicleId) {
      alert("Vehicle ID is missing. Unable to delete.");
      return;
    }
  
    const confirmDelete = window.confirm("Are you sure you want to delete this vehicle?");
    if (confirmDelete) {
      try {
        const response = await axios.delete(`http://localhost:8080/CabService/vehicle?id=${vehicleId}`);
        console.log("Delete response:", response.data);
  
        if (response.data.status === "success") {
          setVehicles((prevVehicles) => prevVehicles.filter(vehicle => vehicle.id !== vehicleId));
          alert("Vehicle deleted successfully!");
        } else {
          alert("Failed to delete vehicle. " + (response.data.message || ""));
        }
      } catch (error) {
        console.error("Error deleting vehicle", error);
        alert("Failed to delete vehicle.");
      }
    }
  };
  
  
  

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
        accountType.current = decodedToken.acc_type; // Store accountType in useRef
      }
    } catch (error) {
      console.error("Invalid token", error);
    }

    const fetchVehicles = async () => {
      try {
        const response = await axios.get("http://localhost:8080/CabService/vehicle");
        console.log(response.data);
        setVehicles(response.data);
      } catch (error) {
        console.error("Error fetching vehicles", error);
      }
    };
    fetchVehicles();
  }, [navigate]); // Empty dependency array to only run once

  const firstLetter = userEmail ? userEmail.charAt(0).toUpperCase() : '';
  
  const getCabClassBadge = (cabClass) => {
    switch(cabClass) {
      case "luxury":
        return <span className="badge bg-dark text-light custom-badge">Luxury</span>; // Dark badge for Luxury
      case "semi-luxury":
        return <span className="badge bg-warning text-dark custom-badge">Semi Luxury</span>; // Yellowish badge for Semi Luxury
      case "economy":
        return <span className="badge bg-primary text-white custom-badge">Economy</span>; // Blue badge for Economy
      case "standard":
        return <span className="badge bg-light text-dark custom-badge">Standard</span>; // Light badge for Standard
      default:
        return <span className="badge bg-light text-dark custom-badge">Standard</span>; // Default to Standard
    }
};

  return (
    <div className="admin-container">
      {sessionExpired && (
        <div className="session-expired-message">
          <p>Your session has expired. Please log in again.</p>
        </div>
      )}
      {!sessionExpired && (
        <>
          <header className="admin-header d-flex justify-content-between align-items-center p-3">
            <div>
              <h1 className="admin-title">Manage Cabs</h1>
              <p className="admin-subtext">View and Manage All the Vehicles here</p>
            </div>
            <div className="admin-profile d-flex align-items-center" ref={profileRef}>
              <div className="profile-icon">{firstLetter}</div>
              <ProfileTag userEmail={userEmail} accountType={accountType.current} /> {/* Use accountType from useRef */}
            </div>
          </header>
          <div className="container mt-4">
            <div className="row">
              <div className="col-12">
                <div className="card full-height-card">
                  <div className="table-responsive" style={{ maxHeight: "550px", overflowY: "auto" }}>
                    <table className="table table-striped">
                      <thead>
                        <tr>
                          <th>Reg No.</th>
                          <th>Brand</th>
                          <th>Model</th>
                          <th>Engine Capacity</th>
                          <th>Vehicle Type</th>
                          <th>Vehicle Color</th>
                          <th>Seat Capacity</th>
                          <th>Number Plate</th>
                          <th>Fuel Type</th>
                          <th>Cab Class</th>
                          <th>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {vehicles.map((vehicle, index) => (
                          <tr key={vehicle.id || index}> {/* Fallback to index if id is undefined */}
                            <td>{vehicle.id ?? "N/A"}</td> {/* Display "N/A" if id is undefined */}
                            <td>{vehicle.brand}</td>
                            <td>{vehicle.model}</td>
                            <td>{vehicle.powersourceCapacity}</td>
                            <td>{vehicle.vehicleType}</td>
                            <td>{vehicle.color}</td>
                            <td>{vehicle.seatCapacity}</td>
                            <td>{vehicle.numberPlate}</td>
                            <td>{vehicle.fuelType}</td>
                            <td>{getCabClassBadge(vehicle.cabClass)}</td>
                            <td>
                              <button className="edit-btn" onClick={() => handleShowEditModal(vehicle)}>
                                <i className="bi bi-gear"></i>
                              </button>
                              <button className="delete-btn" onClick={() => handleDelete(vehicle.id)}>
                                <i className="bi bi-trash"></i>
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div className="floating-btn-container">
            <button className="floating-btn" onClick={handleShow}>
              <i className="bi bi-plus"></i>
            </button>
          </div>
          <AddCabModal show={showModal} onHide={handleClose} />
          <EditCabModal
            show={showEditModal}
            onHide={handleCloseEditModal}
            vehicle={selectedVehicle}
          />
        </>
      )}
    </div>
  );
};

export default AdminCab;
