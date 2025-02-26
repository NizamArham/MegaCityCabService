import React, { useState } from "react";
import { Modal, Button, Form, Card, Row, Col, InputGroup } from "react-bootstrap";

const AddCabModal = ({ show, onHide }) => {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    brand: "",
    model: "",
    fuelType: "",
    powersourceCapacity: "",
    color: "",
    numberPlate: "",
    seatCapacity: "",
    cabClass: "",
    vehicleType: "",
  });
  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const validateStep = () => {
    let currentErrors = {};

    if (step === 1) {
      if (!formData.brand.trim()) currentErrors.brand = "Required";
      if (!formData.model.trim()) currentErrors.model = "Required";
      if (!formData.fuelType.trim()) currentErrors.fuelType = "Required";
      if (!formData.powersourceCapacity.trim()) currentErrors.powersourceCapacity = "Required";
      if (!formData.vehicleType.trim()) currentErrors.vehicleType = "Required";
      if (!formData.color.trim()) currentErrors.color = "Required";
    } else if (step === 2) {
      const numberPlatePattern = /^[A-Za-z]{2,3}-\d{4}$/;
      if (!formData.numberPlate.trim()) {
        currentErrors.numberPlate = "Required";
      } else if (!numberPlatePattern.test(formData.numberPlate)) {
        currentErrors.numberPlate = "Invalid format (e.g., AB-1234)";
      }
    } else if (step === 3) {
      if (!formData.seatCapacity.trim()) {
        currentErrors.seatCapacity = "Required";
      } else if (isNaN(formData.seatCapacity) || formData.seatCapacity < 1) {
        currentErrors.seatCapacity = "Enter a valid seating capacity";
      }
      if (!formData.cabClass.trim()) {
        currentErrors.cabClass = "Required";
      }
    }

    setErrors(currentErrors);
    return Object.keys(currentErrors).length === 0;
  };

  const handleNext = () => {
    if (validateStep()) {
      setStep(prev => prev + 1);
    }
  };

  const handleBack = () => {
    setStep(prev => prev - 1);
  };

  const handleSubmit = async () => {
    if (validateStep()) {
      try {
        const response = await fetch("http://localhost:8080/CabService/vehicle", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(formData),
        });

        if (response.ok) {
          alert("Cab registered successfully!");
          onHide();
          setStep(1);
          setFormData({
            brand: "",
            model: "",
            fuelType: "",
            powersourceCapacity: "",
            color: "",
            numberPlate: "",
            seatCapacity: "",
            cabClass: "",
            vehicleType: "",
          });
          setErrors({});
        } else {
          const errorData = await response.json();
          alert("Failed to register cab. Please try again.");
        }
      } catch (error) {
        alert("An error occurred while submitting the form.");
      }
    }
  };

  const renderStepContent = () => (
    <Card className="p-3">
      {step === 1 && (
        <>
          <h5>Step 1: Vehicle Details</h5>
          <Form.Group controlId="formBrand">
            <Form.Label>Brand</Form.Label>
            <Form.Control
              type="text"
              name="brand"
              value={formData.brand}
              onChange={handleChange}
              isInvalid={!!errors.brand}
            />
            <Form.Control.Feedback type="invalid">{errors.brand}</Form.Control.Feedback>
          </Form.Group>

          <Form.Group controlId="formModelFuel" className="mt-2">
            <Row>
              <Col sm={6}>
                <Form.Label>Model</Form.Label>
                <Form.Control
                  type="text"
                  name="model"
                  value={formData.model}
                  onChange={handleChange}
                  isInvalid={!!errors.model}
                />
                <Form.Control.Feedback type="invalid">{errors.model}</Form.Control.Feedback>
              </Col>
              <Col sm={6}>
                <Form.Label>Fuel Type</Form.Label>
                <Form.Select
                  name="fuelType"
                  value={formData.fuelType}
                  onChange={handleChange}
                  isInvalid={!!errors.fuelType}
                  className="fuel-type-dropdown"
                >
                  <option value="">Select Fuel Type</option>
                  <option value="Petrol">Petrol</option>
                  <option value="Diesel">Diesel</option>
                  <option value="Electric">Electric</option>
                </Form.Select>
                <Form.Control.Feedback type="invalid">{errors.fuelType}</Form.Control.Feedback>
              </Col>
            </Row>
          </Form.Group>

          <Form.Group as={Row} controlId="formEngineCapacity" className="mt-2">
            <Col sm={5}>
              <Form.Label>
                {formData.fuelType === "Electric" ? "Battery Capacity" : "Engine Capacity"}
              </Form.Label>
              <InputGroup hasValidation>
                <Form.Control
                  type="text"
                  name="powersourceCapacity"
                  value={formData.powersourceCapacity}
                  onChange={handleChange}
                  onKeyDown={(e) => {
                    if (!/[0-9]/.test(e.key) && e.key !== "Backspace" && e.key !== "Delete") {
                      e.preventDefault();
                    }
                  }}
                  inputMode="numeric"
                  isInvalid={!!errors.powersourceCapacity}
                />
                <InputGroup.Text>
                  {formData.fuelType === "Electric" ? "kWh" : "cc"}
                </InputGroup.Text>
                <Form.Control.Feedback type="invalid">{errors.powersourceCapacity}</Form.Control.Feedback>
              </InputGroup>
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
                <option value="Tuk">Tuk</option>
                <option value="Bike">Bike</option>
                <option value="Car">Car</option>
                <option value="Van">Van</option>
                <option value="Bus">Bus</option>
              </Form.Select>
              <Form.Control.Feedback type="invalid">{errors.vehicleType}</Form.Control.Feedback>
            </Col>
          </Form.Group>

          <Form.Group controlId="formColor" className="mt-2">
            <Form.Label>Color</Form.Label>
            <Form.Control
              type="text"
              name="color"
              value={formData.color}
              onChange={handleChange}
              isInvalid={!!errors.color}
            />
            <Form.Control.Feedback type="invalid">{errors.color}</Form.Control.Feedback>
          </Form.Group>
        </>
      )}

      {step === 2 && (
        <>
          <h5>Step 2: Number Plate</h5>
          <Form.Group controlId="formNumberPlate">
            <Form.Label>Number Plate</Form.Label>
            <Form.Control
              type="text"
              name="numberPlate"
              value={formData.numberPlate}
              onChange={handleChange}
              isInvalid={!!errors.numberPlate}
            />
            <Form.Control.Feedback type="invalid">{errors.numberPlate}</Form.Control.Feedback>
          </Form.Group>
        </>
      )}

      {step === 3 && (
        <>
          <h5>Step 3: Seating Capacity & Cab Class</h5>
          <Form.Group as={Row} controlId="formSeatCapacity" className="mt-2">
            <Col sm={6}>
              <Form.Label>Seating Capacity</Form.Label>
              <Form.Control
                type="number"
                name="seatCapacity"
                value={formData.seatCapacity}
                onChange={handleChange}
                isInvalid={!!errors.seatCapacity}
              />
              <Form.Control.Feedback type="invalid">{errors.seatCapacity}</Form.Control.Feedback>
            </Col>
            <Col sm={6}>
              <Form.Label>Cab Class</Form.Label>
              <Form.Select
                name="cabClass"
                value={formData.cabClass}
                onChange={handleChange}
                isInvalid={!!errors.cabClass}
              >
                <option value="">Select Cab Class</option>
                <option value="luxury">Luxury</option>
                <option value="semi-luxury">Semi-Luxury</option>
                <option value="normal">Normal</option>
              </Form.Select>
              <Form.Control.Feedback type="invalid">{errors.cabClass}</Form.Control.Feedback>
            </Col>
          </Form.Group>
        </>
      )}
    </Card>
  );

  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>Cab Registration</Modal.Title>
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

export default AddCabModal;
