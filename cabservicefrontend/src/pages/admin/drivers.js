import React, { useEffect, useState, useRef } from "react";
import { jwtDecode } from "jwt-decode";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import "../../css/cab.css";
import { useNavigate } from "react-router-dom";
import ProfileTag from "../profiletag";
import AddDriverModal from "./components/adddrivermodel";
import UpdateDriverModal from "./components/editdrivermodel"; // Import the updated modal
import axios from "axios";

const AdminDrivers = ({ accountType = "Admin" }) => {
  const [userEmail, setUserEmail] = useState("");
  const [sessionExpired, setSessionExpired] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [drivers, setDrivers] = useState([]);
  const [selectedDriver, setSelectedDriver] = useState(null);
  const navigate = useNavigate();
  const profileRef = useRef(null);

  const handleShow = () => setShowModal(true);
  const handleClose = () => setShowModal(false);

  const handleShowEditModal = (driver) => {
    setSelectedDriver(driver); // Set the selected driver for editing
    setShowEditModal(true);    // Open the edit modal
  };

  const handleCloseEditModal = () => {
    setSelectedDriver(null); // Clear selected driver
    setShowEditModal(false); // Close the edit modal
  };

  const deleteDriver = async (email) => {
    const confirmDelete = window.confirm("Are you sure you want to delete this driver?");
    if (!confirmDelete) {
      return;
    }

    try {
      const response = await axios.delete("http://localhost:8080/CabService/drivers", {
        data: { email },
      });

      if (response.data.status === "success") {
        setDrivers(drivers.filter((driver) => driver.email !== email)); // Remove the deleted driver from the list
        alert("Driver deleted successfully!");
      } else {
        alert("Failed to delete driver.");
      }
    } catch (error) {
      console.error("Error deleting driver", error);
      alert("An error occurred while deleting the driver.");
    }
  };

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

    // Fetch drivers
    const fetchDrivers = async () => {
      try {
        const response = await axios.get("http://localhost:8080/CabService/drivers");
        console.log("Fetched drivers:", response.data); // Log the fetched data
        setDrivers(response.data);
      } catch (error) {
        console.error("Error fetching drivers", error);
      }
    };

    fetchDrivers();

    const handleClickOutside = (event) => {
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        // Close dropdown if necessary
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
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
        <>
          <header className="admin-header d-flex justify-content-between align-items-center p-3">
            <div>
              <h1 className="admin-title">Manage Drivers</h1>
              <p className="admin-subtext">View and Manage All the Drivers here</p>
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
                  <div className="table-responsive" style={{ maxHeight: "550px", overflowY: "auto" }}>
                    <table className="table table-striped">
                      <thead>
                        <tr>
                          <th>#</th>
                          <th>First Name</th>
                          <th>Last Name</th>
                          <th>NIC</th>
                          <th>Telephone</th>
                          <th>Email</th>
                          <th>Account Status</th>
                          <th>Assigned Vehicle</th>
                          <th>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {drivers.map((driver, index) => (
                          <tr key={driver.id || index}>
                            <td>{index + 1}</td>
                            <td>{driver.firstName}</td>
                            <td>{driver.lastName}</td>
                            <td>{driver.nic}</td>
                            <td>{driver.tp}</td>
                            <td>{driver.email}</td>
                            <td style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '5px' }}>
                              {driver.accountStatus === "active" ? (
                                <>
                                  <span style={{ color: '#25D366', fontSize: '30px' }}>•</span>
                                  Active
                                </>
                              ) : (
                                driver.accountStatus
                              )}
                            </td>
                            <td>{driver.assignedVehicle ?? "None"}</td>
                            <td>
                              <button className="edit-btn" onClick={() => handleShowEditModal(driver)}>
                                <i className="bi bi-gear"></i>
                              </button>
                              <button className="delete-btn" onClick={() => deleteDriver(driver.email)}>
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
          <AddDriverModal show={showModal} onHide={handleClose} />
          <UpdateDriverModal
            show={showEditModal}
            onHide={handleCloseEditModal}
            driver={selectedDriver} // Pass the selected driver to the modal
          />
        </>
      )}
    </div>
  );
};

export default AdminDrivers;
