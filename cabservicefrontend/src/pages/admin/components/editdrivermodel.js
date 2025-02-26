import React, { useState, useEffect } from "react";
import { Modal, Button, Form, Card, Row, Col } from "react-bootstrap";

const UpdateDriverModal = ({ show, onHide, driver }) => {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    nic: "",
    tp: "",
    email: "",
    assignedVehicle: "assign_later",
  });
  const [errors, setErrors] = useState({});
  const [vehicleOptions, setVehicleOptions] = useState([
    { id: "assign_later", label: "Assign Later" },
  ]);

  useEffect(() => {
    if (show && driver) {
      // Initialize form data with the selected driver's details
      setFormData({
        firstName: driver.firstName || "",
        lastName: driver.lastName || "",
        nic: driver.nic || "",
        tp: driver.tp || "",
        email: driver.email || "",
        assignedVehicle: driver.assignedVehicle || "assign_later",
      });
      fetchVehicles();
    } else {
      resetForm();
    }
  }, [show, driver]);

  const fetchVehicles = async () => {
    try {
      const response = await fetch("http://localhost:8080/CabService/availablevehicles");
      if (!response.ok) throw new Error("Failed to fetch vehicles");
      const vehicles = await response.json();

      setVehicleOptions([
        { id: "assign_later", label: "Assign Later" },
        ...vehicles.map((vehicle) => ({
          id: vehicle.id,
          label: `${vehicle.vehicleType} - ${vehicle.licensePlate} - ${vehicle.brand}`,
        })),
      ]);
    } catch (error) {
      console.error("Error fetching vehicles:", error);
    }
  };

  const handleChange = (e) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const resetForm = () => {
    setFormData({
      firstName: "",
      lastName: "",
      nic: "",
      tp: "",
      email: "",
      assignedVehicle: "assign_later",
    });
    setStep(1);
    setErrors({});
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
        const response = await fetch(`http://localhost:8080/CabService/driver/${driver.id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(formData),
        });

        if (response.ok) {
          alert("Driver details updated successfully!");
          onHide();
          resetForm();
        } else {
          alert("Failed to update driver details. Please try again.");
        }
      } catch (error) {
        alert("An error occurred while updating the details.");
      }
    }
  };

  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>Update Driver Details</Modal.Title>
      </Modal.Header>
      <Modal.Body>
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
                <Form.Select name="assignedVehicle" value={formData.assignedVehicle} onChange={handleChange}>
                  {vehicleOptions.map((vehicle) => (
                    <option key={vehicle.id} value={vehicle.id}>{vehicle.label}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </>
          )}
        </Card>
      </Modal.Body>
      <Modal.Footer>
        {step > 1 && <Button variant="secondary" onClick={handleBack}>Back</Button>}
        {step < 2 && <Button variant="primary" onClick={handleNext}>Continue</Button>}
        {step === 2 && <Button variant="success" onClick={handleSubmit}>Update</Button>}
      </Modal.Footer>
    </Modal>
  );
};

export default UpdateDriverModal;