import React, { useState } from "react";
import ApiService from "../../service/ApiService"; // Import the ApiService for interacting with the backend API

const FindBookingPage = () => {
  const [confirmationCode, setConfirmationCode] = useState(""); // State for storing the user input (booking confirmation code)

  const [bookingDetails, setBookingDetails] = useState(null); // State for storing the booking details fetched from the API

  const [error, setError] = useState(null); // State for storing error messages, if any

  // Function to handle the search for booking details
  const handleSearch = async () => {
    
    if (!confirmationCode.trim()) { // Check if the confirmation code is empty or only contains spaces
      setError("Please Enter a booking confirmation code"); // Set an error message
      setTimeout(() => setError(""), 5000); // Clear the error after 5 seconds
      return;
    }

    try {
      // Call the API to fetch booking details by the confirmation code
      const response = await ApiService.getBookingByConfirmationCode(confirmationCode);
      setBookingDetails(response.booking); // Update state with the retrieved booking details
      setError(null); // Clear any previous error messages
      
    } catch (error) {
      // Handle errors: show the error message from the API response or fallback to a generic message
      setError(error.response?.data?.message || error.message);
      setTimeout(() => setError(""), 5000); // Clear the error after 5 seconds
    }
  };

  return (
    <div className="find-booking-page">
      <h2>Find Booking</h2>

      {/* Search input and button */}
      <div className="search-container">
        <input
          required // Ensures the input is not empty
          type="text" // Input type is text
          placeholder="Enter your booking confirmation code" // Placeholder text for the input field
          value={confirmationCode} // Binds the input value to the state
          onChange={(e) => setConfirmationCode(e.target.value)} // Updates the state on input change
        />
        <button onClick={handleSearch}>Find</button>{" "}
        {/* Calls the handleSearch function when clicked */}
      </div>

      {/* Display error messages in red, if any */}
      {error && <p style={{ color: "red" }}>{error}</p>}

      {/* Display booking details if data is available */}
      {bookingDetails && (
        <div className="booking-details">
          <h3>Booking Details</h3>
          <p>Confirmation Code: {bookingDetails.bookingConfirmationCode}</p>
          <p>Check-in Date: {bookingDetails.checkInDate}</p>
          <p>Check-out Date: {bookingDetails.checkOutDate}</p>
          <p>Num Of Adults: {bookingDetails.numOfAdults}</p>
          <p>Num Of Children: {bookingDetails.numOfChildren}</p>

          <br />
          <hr />
          <br />

          {/* Section for booker's details */}
          <h3>Booker Details</h3>
          <div>
            <p>Name: {bookingDetails.user.name}</p>
            <p>Email: {bookingDetails.user.email}</p>
            <p>Phone Number: {bookingDetails.user.phoneNumber}</p>
          </div>

          <br />
          <hr />
          <br />

          {/* Section for room details */}
          <h3>Room Details</h3>
          <div>
            <p>Room Type: {bookingDetails.room.roomType}</p>
            <img
              src={bookingDetails.room.roomPhotoUrl} // Image URL for the room
              alt="" // Alt text in case the image fails to load
              sizes="" // Optional sizes attribute (can be removed if unused)
              srcSet="" // Optional srcSet attribute (can be removed if unused)
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default FindBookingPage;
