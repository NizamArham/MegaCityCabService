import React, { useState } from "react";
import { Modal, Button, Form, Card } from "react-bootstrap";

const AddRouteModal = ({ show, onHide }) => {
  const [formData, setFormData] = useState({
    locationA: "",
    locationB: "",
    distance: "",
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const validateForm = () => {
    let currentErrors = {};
    if (!formData.locationA.trim()) currentErrors.locationA = "Required";
    if (!formData.locationB.trim()) currentErrors.locationB = "Required";
    if (!formData.distance.trim() || isNaN(formData.distance)) {
      currentErrors.distance = "Enter a valid number";
    }
    setErrors(currentErrors);
    return Object.keys(currentErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (validateForm()) {
      try {
        const response = await fetch("http://localhost:8080/CabService/routes", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(formData),
        });

        const responseData = await response.json();
        if (response.ok) {
          alert(responseData.message);
          onHide();
          setFormData({ locationA: "", locationB: "", distance: "" });
          setErrors({});
        } else {
          alert(responseData.message);
        }
      } catch (error) {
        alert("Error adding route");
      }
    }
  };

  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>Add New Route</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Card className="p-3">
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Location A</Form.Label>
              <Form.Control
                type="text"
                name="locationA"
                value={formData.locationA}
                onChange={handleChange}
                isInvalid={!!errors.locationA}
              />
              <Form.Control.Feedback type="invalid">
                {errors.locationA}
              </Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Location B</Form.Label>
              <Form.Control
                type="text"
                name="locationB"
                value={formData.locationB}
                onChange={handleChange}
                isInvalid={!!errors.locationB}
              />
              <Form.Control.Feedback type="invalid">
                {errors.locationB}
              </Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Distance (km)</Form.Label>
              <Form.Control
                type="text"
                name="distance"
                value={formData.distance}
                onChange={handleChange}
                isInvalid={!!errors.distance}
              />
              <Form.Control.Feedback type="invalid">
                {errors.distance}
              </Form.Control.Feedback>
            </Form.Group>
          </Form>
        </Card>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide}>Cancel</Button>
        <Button variant="success" onClick={handleSubmit}>Add Route</Button>
      </Modal.Footer>
    </Modal>
  );
};

export default AddRouteModal;
