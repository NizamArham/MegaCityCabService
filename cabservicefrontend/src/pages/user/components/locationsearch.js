import React, { useState, useEffect } from "react";

const LocationSearch = () => {
  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [selectedIndex, setSelectedIndex] = useState(-1);

  useEffect(() => {
    if (query.length > 1) {
      fetch(`http://localhost:8080/CabService/locations?query=${query}`)
        .then((res) => res.json())
        .then((data) => setSuggestions(data))
        .catch((err) => console.error("Error fetching locations:", err));
    } else {
      setSuggestions([]);
    }
  }, [query]);

  const handleSelect = (location) => {
    setQuery(location);
    setSuggestions([]); // Hide suggestions after selecting
  };

  const handleKeyDown = (e) => {
    if (e.key === "ArrowDown") {
      setSelectedIndex((prevIndex) => Math.min(prevIndex + 1, suggestions.length - 1));
    } else if (e.key === "ArrowUp") {
      setSelectedIndex((prevIndex) => Math.max(prevIndex - 1, 0));
    } else if (e.key === "Tab" && selectedIndex !== -1) {
      setQuery(suggestions[selectedIndex]);
      setSuggestions([]);
      e.preventDefault(); // Prevent the default tab behavior
    }
  };

  return (
    <div style={{ position: "relative", width: "100%" }}>
      <label>Pickup Location:</label>
      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        onKeyDown={handleKeyDown}
        placeholder="Start typing..."
        className="form-control"
        style={{ width: "100%", paddingRight: "30px" }} // Add padding for dropdown
      />

      {suggestions.length > 0 && (
        <ul
          className="suggestions-list"
          style={{
            position: "absolute",
            top: "100%",
            left: 0,
            width: "100%",
            maxHeight: "150px",
            overflowY: "auto",
            backgroundColor: "white",
            border: "1px solid #ddd",
            marginTop: "5px",
            padding: "0",
            zIndex: 1,
            boxSizing: "border-box",
            listStyleType: "none",
            paddingLeft: "5px",
            paddingRight: "5px",
          }}
        >
          {suggestions.map((location, index) => (
            <li
              key={index}
              onClick={() => handleSelect(location)}
              style={{
                backgroundColor: selectedIndex === index ? "#f0f0f0" : "transparent",
                color: "#aaa", // Light color for the suggestions
                padding: "5px",
                cursor: "pointer",
                borderBottom: "1px solid #ddd", // Add border between suggestions
              }}
            >
              {location}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default LocationSearch;
