import React, { useEffect, useState, useRef } from "react";
import { jwtDecode } from "jwt-decode";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import ProfileTag from "./profiletag";
import AddCabModal from "./addcarmodel.js";
import "../../css/cab.css";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const AdminCab = ({ accountType = "Admin" }) => {
  const [userEmail, setUserEmail] = useState("");
  const [sessionExpired, setSessionExpired] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [vehicles, setVehicles] = useState([]);
  const navigate = useNavigate();
  const profileRef = useRef(null);

  const handleShow = () => setShowModal(true);
  const handleClose = () => setShowModal(false);

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

    // Fetch vehicle data from backend
    const fetchVehicles = async () => {
      try {
        const response = await axios.get("http://localhost:8080/CabServiceBackend/vehicle");
        setVehicles(response.data);
      } catch (error) {
        console.error("Error fetching vehicles", error);
      }
    };
    fetchVehicles();
  }, [navigate]);

  const firstLetter = userEmail ? userEmail.charAt(0).toUpperCase() : '';

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
              <ProfileTag userEmail={userEmail} accountType={accountType} />
            </div>
          </header>
          <div className="container mt-4">
            <div className="row">
              <div className="col-12">
                <div className="card full-height-card">
                  <div className="card-body d-flex flex-column justify-content-between">
                    {/* Vehicle Table */}
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
                          <th>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {vehicles.map((vehicle) => (
                          <tr key={vehicle.id}>
                            <td>{vehicle.id}</td>
                            <td>{vehicle.brand}</td>
                            <td>{vehicle.model}</td>
                            <td>{vehicle.engineCapacity}</td>
                            <td>{vehicle.vehicleType}</td>
                            <td>{vehicle.color}</td>
                            <td>{vehicle.seatCapacity}</td>
                            <td>{vehicle.numberPlate}</td>
                            <td>
                              <button className="view-btn">
                                <i className="bi bi-eye"></i> View
                              </button>
                              <button className="edit-btn">
                                <i className="bi bi-pen"></i> Edit
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
        </>
      )}
    </div>
  );
};

export default AdminCab;
