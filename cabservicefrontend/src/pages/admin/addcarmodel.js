import React, { useState } from "react";
import { Modal, Button, Form, Card,Row, Col  } from "react-bootstrap";

const AddCabModal = ({ show, onHide }) => {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    brand: "",
    model: "",
    engineCapacity: "",
    color: "",
    numberPlate: "",
    seatCapacity: "",
  });
  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const validateStep = () => {
    let currentErrors = {};
    if (step === 1) {
      if (!formData.brand.trim()) currentErrors.brand = "required";
      if (!formData.model.trim()) currentErrors.model = "required";
      if (!formData.engineCapacity.trim()) currentErrors.engineCapacity = "required";
      if (!formData.vehicleType || !formData.vehicleType.trim()) {
        currentErrors.vehicleType = "Vehicle type is required";
      }
      if (!formData.color.trim()) currentErrors.color = "required";
    } else if (step === 2) {
      const numberPlatePattern = /^[A-Za-z]{2,3}-\d{4}$/;
      if (!formData.numberPlate.trim()) {
        currentErrors.numberPlate = "required";
      } else if (!numberPlatePattern.test(formData.numberPlate)) {
        currentErrors.numberPlate = "Invalid number plate format (e.g., AB-1234)";
      }
    } else if (step === 3) {
      if (!formData.seatCapacity.trim()) {
        currentErrors.seatCapacity = "required";
      } else if (isNaN(formData.seatCapacity) || formData.seatCapacity < 1) {
        currentErrors.seatCapacity = "Enter a valid seating capacity";
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
        const response = await fetch("http://localhost:8080/CabServiceBackend/vehicle", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(formData),
        });
  
        if (response.ok) {
          console.log("Cab registered successfully");
          alert("Cab registered successfully!");
  
          // Reset form and close modal
          onHide();
          setStep(1);
          setFormData({
            brand: "",
            model: "",
            engineCapacity: "",
            color: "",
            numberPlate: "",
            seatCapacity: "",
          });
          setErrors({});
        } else {
          const errorData = await response.json();
          console.error("Error registering cab:", errorData);
          alert("Failed to register cab. Please try again.");
          console.log("Form Data:", formData);
        }
      } catch (error) {
        console.error("Error submitting form:", error);
        alert("An error occurred while submitting the form.");
        console.log("Form Data:", formData);
      }
    }
  };
  

  const renderStepContent = () => {
    return (
      <Card className="p-3">
        {step === 1 && (
         <>
         <h5>Step 1: Vehicle Details</h5>
         <Form.Group controlId="formBrand">
           <Form.Label>Brand</Form.Label>
           <Form.Control type="text" name="brand" value={formData.brand} onChange={handleChange} isInvalid={!!errors.brand} />
           <Form.Control.Feedback type="invalid">{errors.brand}</Form.Control.Feedback>
         </Form.Group>
         <Form.Group controlId="formModel" className="mt-2">
           <Form.Label>Model</Form.Label>
           <Form.Control type="text" name="model" value={formData.model} onChange={handleChange} isInvalid={!!errors.model} />
           <Form.Control.Feedback type="invalid">{errors.model}</Form.Control.Feedback>
         </Form.Group>

         <Form.Group as={Row} controlId="formEngineCapacity" className="mt-2">
  <Col sm={5}>
    <Form.Label>Engine Capacity</Form.Label>
    <Form.Control 
      type="text" 
      name="engineCapacity" 
      value={formData.engineCapacity} 
      onChange={handleChange} 
      isInvalid={!!errors.engineCapacity} 
    />
    <Form.Control.Feedback type="invalid">
      {errors.engineCapacity}
    </Form.Control.Feedback>
  </Col>
  <Col sm={5}>
    <Form.Label>Vehicle Type</Form.Label>
    <Form.Select 
      name="vehicleType" 
      value={formData.vehicleType} 
      onChange={handleChange} 
      isInvalid={!!errors.vehicleType}
    >
      <option value="">Select Vehicle Type</option>
      <option value="tuk">Tuk</option>
      <option value="bike">Bike</option>
      <option value="car">Car</option>
      <option value="van">Van</option>
      <option value="bus">Bus</option>
    </Form.Select>
    <Form.Control.Feedback type="invalid">
      {errors.vehicleType}
    </Form.Control.Feedback>
  </Col>
</Form.Group>




         <Form.Group controlId="formColor" className="mt-2">
           <Form.Label>Color</Form.Label>
           <Form.Control type="text" name="color" value={formData.color} onChange={handleChange} isInvalid={!!errors.color} />
           <Form.Control.Feedback type="invalid">{errors.color}</Form.Control.Feedback>
         </Form.Group>
       </>
        )}
        {step === 2 && (
          <>
            <h5>Step 2: Number Plate</h5>
            <Form.Group controlId="formNumberPlate">
              <Form.Label>Number Plate</Form.Label>
              <Form.Control type="text" name="numberPlate" value={formData.numberPlate} onChange={handleChange} isInvalid={!!errors.numberPlate} />
              <Form.Control.Feedback type="invalid">{errors.numberPlate}</Form.Control.Feedback>
            </Form.Group>
          </>
        )}
        {step === 3 && (
          <>
            <h5>Step 3: Seating Capacity</h5>
            <Form.Group controlId="formSeatCapacity">
              <Form.Label>Seating Capacity</Form.Label>
              <Form.Control type="number" name="seatCapacity" value={formData.seatCapacity} onChange={handleChange} isInvalid={!!errors.seatCapacity} />
              <Form.Control.Feedback type="invalid">{errors.seatCapacity}</Form.Control.Feedback>
            </Form.Group>
          </>
        )}
      </Card>
    );
  };

  return (
    <Modal show={show} onHide={onHide} centered>
    <Modal.Header closeButton>
    <Modal.Title>Cab Registration</Modal.Title>
    </Modal.Header>
    <Modal.Body>
      {renderStepContent()}
    </Modal.Body>
    <Modal.Footer>
      {step > 1 && <Button variant="secondary" onClick={handleBack}>Back</Button>}
      {step < 3 && <Button variant="primary" onClick={handleNext}>Continue</Button>}
      {step === 3 && <Button variant="success" onClick={handleSubmit}>Finish </Button>}
    </Modal.Footer>
  </Modal>

  );
};

export default AddCabModal;