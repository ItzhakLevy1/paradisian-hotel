import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom"; // React Router hook for navigation
import ApiService from "../../service/ApiService"; // Service to handle API requests

const EditProfilePage = () => {
  // State to store user data (profile details)
  const [user, setUser] = useState(null);

  // State to store error messages
  const [error, setError] = useState(null);

  // State to store the edited name
  const [editedName, setEditedName] = useState("");

  // State to store success messages
  const [success, setSuccess] = useState(null);

  // State to disable the save button while saving
  const [isSaving, setIsSaving] = useState(false);

  const navigate = useNavigate(); // Hook to programmatically navigate between routes

  // Fetch user profile details when the component mounts
  useEffect(() => {
    const fetchUserProfile = async () => {
      try {
        // Call API to fetch user profile
        const response = await ApiService.getUserProfile();
        setUser(response.user); // Store the user details in the state
        setEditedName(response.user.name); // Initialize the edited name with the current name
      } catch (error) {
        // Handle any errors during the API call
        setError(error.message);
      }
    };

    fetchUserProfile(); // Trigger the fetch function
  }, []); // Empty dependency array ensures this runs only once when the component is mounted

  // Handle saving the edited name
  const handleSaveName = async () => {
    // Trim the input to avoid spaces being considered valid input
    if (!editedName.trim()) {
      setError("Name cannot be empty");
      setSuccess(null);
      return;
    }

    setIsSaving(true); // Disable the save button while saving

    try {
      // Call the API to update the user's name
      const updatedUser = await ApiService.updateUserProfile({
        ...user,
        name: editedName.trim(),
      });

      setUser(updatedUser); // Update state with the updated user data
      setError(null); // Clear previous errors
      setSuccess("Profile updated successfully!"); // Set success message

      // Redirect to the profile page after a short delay
      setTimeout(() => {
        navigate("/profile"); // Adjust the path as needed
      }, 2000);
    } catch (error) {
      setError(error.message); // Handle API errors
      setSuccess(null);
    } finally {
      setIsSaving(false); // Re-enable the save button
    }
  };

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
        setError("Please contact admin to request profile deletion");
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

      {/* Display a success message if there is one */}
      {success && <p className="success-message">{success}</p>}

      {/* Display user details if the user data is available */}
      {user && (
        <div className="profile-details">
          {/* Display the user's name */}
          <p>
            <strong>Name: </strong>
            <input
              type="text"
              value={editedName}
              onChange={(e) => setEditedName(e.target.value)}
            />
            <button onClick={handleSaveName} disabled={isSaving}>
              {isSaving ? "Saving..." : "Save"}
            </button>
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
