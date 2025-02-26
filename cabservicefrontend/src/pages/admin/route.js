import React, { useEffect, useState, useRef } from "react";
import { jwtDecode } from "jwt-decode";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import { useNavigate } from "react-router-dom";
import ProfileTag from "../profiletag";
import AddRouteModal from "./components/addroutemodel";

const AdminRoutes = ({ accountType = "Admin" }) => {
  const [userEmail, setUserEmail] = useState("");
  const [sessionExpired, setSessionExpired] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [routes, setRoutes] = useState([]);
  const [userAccountType, setUserAccountType] = useState(accountType);

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
        setUserAccountType(decodedToken.acc_type);
      }
    } catch (error) {
      console.error("Invalid token", error);
    }
  }, [navigate]);

  useEffect(() => {
    fetch("http://localhost:8080/CabService/routes")
      .then((response) => response.json())
      .then((data) => setRoutes(data))
      .catch((error) => console.error("Error fetching routes:", error));
  }, []);

  const deleteRoute = (id) => {
    if (!window.confirm("Are you sure you want to delete this route?")) return;

    fetch(`http://localhost:8080/CabService/routes?id=${id}`, {
      method: "DELETE",
    })
      .then((response) => {
        if (response.ok) {
          setRoutes((prevRoutes) => prevRoutes.filter((route) => route.id !== id));
        } else {
          console.error("Failed to delete route");
        }
      })
      .catch((error) => console.error("Error deleting route:", error));
  };

  const firstLetter = userEmail.charAt(0).toUpperCase();

  return (
    <div className="admin-container">
      {sessionExpired ? (
        <div className="session-expired-message">
          <p>Your session has expired. Please log in again.</p>
        </div>
      ) : (
        <>
          <header className="admin-header d-flex justify-content-between align-items-center p-3">
            <div>
              <h1 className="admin-title">Manage Route</h1>
              <p className="admin-subtext">View and Manage All the Routes here</p>
            </div>
            <div className="admin-profile d-flex align-items-center" ref={profileRef}>
              <div className="profile-icon">{firstLetter}</div>
              <ProfileTag userEmail={userEmail} accountType={userAccountType} />
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
                          <th>ID</th>
                          <th>Location A</th>
                          <th>Location B</th>
                          <th>Distance [km]</th>
                          <th>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {routes.map((route) => (
                          <tr key={route.id}>
                            <td>{route.id}</td>
                            <td>{route.locationA}</td>
                            <td>{route.locationB}</td>
                            <td>{route.distance} km</td>
                            <td>
                              <button
                                className="btn btn-danger btn-sm"
                                onClick={() => deleteRoute(route.id)}
                              >
                                <i className="bi bi-trash"></i> Delete
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
            <button className="floating-btn" onClick={() => setShowModal(true)}>
              <i className="bi bi-plus"></i>
            </button>
          </div>

          <AddRouteModal show={showModal} onHide={() => setShowModal(false)} />
        </>
      )}
    </div>
  );
};

export default AdminRoutes;
