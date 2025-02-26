import React, { useState, useEffect } from "react";
import { Modal, Button, Form, Card, Row, Col } from "react-bootstrap";

const AddDriverModal = ({ show, onHide }) => {
  const [step, setStep] = useState(1);
  
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    nic: "",
    tp: "",
    email: "",
    assignedVehicle: "not assigned",
    password: "",
    confirmPassword: "",
  });

  const [errors, setErrors] = useState({});
  const [vehicleOptions, setVehicleOptions] = useState([
    { id: "not assigned", label: "Assign Later" },
  ]);

  useEffect(() => {
    if (show) {
      fetchVehicles();
    }
  }, [show]);
  
  const fetchVehicles = async () => {
    try {
      const response = await fetch("http://localhost:8080/CabService/availablevehicles");
      if (!response.ok) throw new Error("Failed to fetch vehicles");
  
      const vehicles = await response.json();
      console.log("Fetched vehicles:", vehicles);
  
      const availableVehicles = vehicles.map((vehicle) => ({
        id: vehicle.id,
        licensePlate: vehicle.licensePlate, // Include licensePlate in the options
        label: `${vehicle.vehicleType} - ${vehicle.licensePlate} - ${vehicle.brand}`,
      }));
  
      console.log("Available Vehicles:", availableVehicles);
  
      setVehicleOptions([{ id: "not assigned", label: "Assign Later" }, ...availableVehicles]);
    } catch (error) {
      console.error("Error fetching vehicles:", error);
    }
  };
  
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const validateStep = () => {
    let currentErrors = {};
    if (step === 1) {
      if (!formData.firstName.trim()) currentErrors.firstName = "Required";
      if (!formData.lastName.trim()) currentErrors.lastName = "Required";
      if (!formData.tp.trim()) currentErrors.tp = "Required";
      if (!formData.email.trim()) currentErrors.email = "Required";
      if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(formData.email)) {
        currentErrors.email = "Invalid email format";
      }
      if (!formData.nic.trim()) {
        currentErrors.nic = "Required";
      } else if (!/^\d{9}[vxVX]?$/.test(formData.nic) && !/^\d{12}$/.test(formData.nic)) {
        currentErrors.nic = "Invalid NIC format";
      }
    } else if (step === 3) {
      if (!formData.password.trim()) {
        currentErrors.password = "Password is required";
      } else if (formData.password.length < 6) {
        currentErrors.password = "Password must be at least 6 characters";
      }
      if (!formData.confirmPassword.trim()) {
        currentErrors.confirmPassword = "Confirm Password is required";
      } else if (formData.password !== formData.confirmPassword) {
        currentErrors.confirmPassword = "Passwords do not match";
      }
    }
    setErrors(currentErrors);
    return Object.keys(currentErrors).length === 0;
  };

  const handleNext = () => {
    if (validateStep()) {
      setStep((prev) => prev + 1);
    }
  };

  const handleBack = () => {
    setStep((prev) => prev - 1);
  };


  const handleSubmit = async () => {
    if (validateStep()) {
      try {
        const assignedVehicle = formData.assignedVehicle === "not assigned" 
          ? "not assigned" 
          : formData.assignedVehicle;
  
        const response = await fetch("http://localhost:8080/CabService/drivers", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            firstName: formData.firstName,
            lastName: formData.lastName,
            nic: formData.nic,
            tp: formData.tp,
            email: formData.email,
            password: formData.password,
            assignedVehicle: assignedVehicle,
            role: "driver", 
          }),
        });
  
        const responseData = await response.json();
        console.log("Backend Response:", responseData);
  
        if (response.ok) {
          if (responseData.status === "success") {
            alert(responseData.message);
            onHide();
            setStep(1);
            setFormData({
              firstName: "",
              lastName: "",
              nic: "",
              tp: "",
              email: "",
              assignedVehicle: "not assigned",
              password: "",
              confirmPassword: "",
            });
            setErrors({});
          } else {
            alert(responseData.message);
          }
        } else {
          alert(responseData.message);
        }
      } catch (error) {
        alert(error.message);
      }
    }
  };
  

  const renderStepContent = () => {
    return (
      <Card className="p-3">
        {step === 1 && (
          <>
            <h5>Step 1: Driver Details</h5>
            <Row>
              <Col>
                <Form.Group>
                  <Form.Label>First Name</Form.Label>
                  <Form.Control type="text" name="firstName" value={formData.firstName} onChange={handleChange} isInvalid={!!errors.firstName} />
                  <Form.Control.Feedback type="invalid">{errors.firstName}</Form.Control.Feedback>
                </Form.Group>
              </Col>
              <Col>
                <Form.Group>
                  <Form.Label>Last Name</Form.Label>
                  <Form.Control type="text" name="lastName" value={formData.lastName} onChange={handleChange} isInvalid={!!errors.lastName} />
                  <Form.Control.Feedback type="invalid">{errors.lastName}</Form.Control.Feedback>
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mt-2">
              <Form.Label>NIC</Form.Label>
              <Form.Control type="text" name="nic" value={formData.nic} onChange={handleChange} isInvalid={!!errors.nic} />
              <Form.Control.Feedback type="invalid">{errors.nic}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mt-2">
              <Form.Label>Telephone</Form.Label>
              <Form.Control type="text" name="tp" value={formData.tp} onChange={handleChange} isInvalid={!!errors.tp} />
              <Form.Control.Feedback type="invalid">{errors.tp}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mt-2">
              <Form.Label>Email</Form.Label>
              <Form.Control type="email" name="email" value={formData.email} onChange={handleChange} isInvalid={!!errors.email} />
              <Form.Control.Feedback type="invalid">{errors.email}</Form.Control.Feedback>
            </Form.Group>
          </>
        )}
        {step === 2 && (
          <>
            <h5>Step 2: Assign Vehicle</h5>
            <Form.Group>
              <Form.Label>Select Vehicle</Form.Label>
              <Form.Select
                name="assignedVehicle"
                value={formData.assignedVehicle}
                onChange={handleChange}
                style={{ width: '100%' }} // Adjust the width to fit your needs
              >
                {vehicleOptions.map((vehicle) => (
                  <option key={vehicle.id} value={vehicle.licensePlate}>
                    {vehicle.label}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

          </>
        )}
        {step === 3 && (
          <>
            <h5>Step 3: Set Password</h5>
            <Form.Group className="mt-2">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                isInvalid={!!errors.password}
              />
              <Form.Control.Feedback type="invalid">{errors.password}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mt-2">
              <Form.Label>Confirm Password</Form.Label>
              <Form.Control
                type="password"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                isInvalid={!!errors.confirmPassword}
              />
              <Form.Control.Feedback type="invalid">{errors.confirmPassword}</Form.Control.Feedback>
            </Form.Group>
          </>
        )}
      </Card>
    );
  };

  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>Driver Registration</Modal.Title>
      </Modal.Header>
      <Modal.Body>{renderStepContent()}</Modal.Body>
      <Modal.Footer>
        {step > 1 && <Button variant="secondary" onClick={handleBack}>Back</Button>}
        {step < 3 && <Button variant="primary" onClick={handleNext}>Continue</Button>}
        {step === 3 && <Button variant="success" onClick={handleSubmit}>Finish</Button>}
      </Modal.Footer>
    </Modal>
  );
};

export default AddDriverModal;
