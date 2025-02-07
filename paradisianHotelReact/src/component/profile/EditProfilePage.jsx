import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom"; // React Router hook for navigation
import ApiService from "../../service/ApiService"; // Service to handle API requests

const EditProfilePage = () => {
  // State to store user data (profile details)
  const [user, setUser] = useState(null);

  // State to store error messages
  const [error, setError] = useState(null);

  const navigate = useNavigate(); // Hook to programmatically navigate between routes

  // Fetch user profile details when the component mounts
  useEffect(() => {
    const fetchUserProfile = async () => {
      try {
        // Call API to fetch user profile
        const response = await ApiService.getUserProfile();
        setUser(response.user); // Store the user details in the state
      } catch (error) {
        // Handle any errors during the API call
        setError(error.message);
      }
    };

    fetchUserProfile(); // Trigger the fetch function
  }, []); // Empty dependency array ensures this runs only once when the component is mounted

  // Handle deleting the user's profile
  const handleDeleteProfile = async () => {
    // Confirm with the user before proceeding with deletion
    if (!window.confirm("Are you sure you want to delete your account?")) {
      return; // Exit if the user cancels
    }
    try {
      // Call the API to delete the user's profile
      await ApiService.deleteUser(user.id);
      ApiService.logout(); // Log the user out
      navigate("/home"); // Redirect the user to the home page after deletion
    } catch (error) {
      // Handle any errors during the API call
      if (error.response && error.response.status === 403) {
        setError('Please contact admin to request profile deletion');
      } else {
        setError(error.message);
      }
    }
  };

  return (
    <div className="edit-profile-page">
      <h2>Edit Profile</h2>

      {/* Display an error message if there is one */}
      {error && <p className="error-message">{error}</p>}

      {/* Display user details if the user data is available */}
      {user && (
        <div className="profile-details">
          {/* Display the user's name */}
          <p>
            <strong>Name:</strong> {user.name}
          </p>

          {/* Display the user's email */}
          <p>
            <strong>Email:</strong> {user.email}
          </p>

          {/* Display the user's phone number */}
          <p>
            <strong>Phone Number:</strong> {user.phoneNumber}
          </p>

          {/* Button to delete the user's profile */}
          <button
            className="delete-profile-button"
            onClick={handleDeleteProfile}
          >
            Delete Profile
          </button>
        </div>
      )}
    </div>
  );
};

export default EditProfilePage;
