import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom"; // React Router hooks for navigation and location
import ApiService from "../../service/ApiService"; // API service for server communication
import Spinner from "react-bootstrap/Spinner"; // Import Spinner from react-bootstrap

function LoginPage() {
  // State variables to store form inputs and errors
  const [email, setEmail] = useState(""); // User's email address
  const [password, setPassword] = useState(""); // User's password
  const [error, setError] = useState(""); // Error message for login issues
  const [loading, setLoading] = useState(false); // Loading state for spinner

  // React Router hooks for navigation and access to the current location
  const navigate = useNavigate(); // To programmatically navigate to other routes
  const location = useLocation(); // To access the current route's location details

  // Determines where to navigate after login (fallback to "/home" if no previous page is specified)
  const from = location.state?.from?.pathname || "/home";

  // Handles form submission for login
  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent the default form submission behavior (e.g., page reload)

    // Ensure both email and password are provided
    if (!email || !password) {
      setError("Please fill in all fields."); // Show an error if fields are empty
      setTimeout(() => setError(""), 5000); // Clear the error after 5 seconds
      return;
    }

    setLoading(true); // Set loading state to true before making the request

    try {
      // Call the API to log in the user
      const response = await ApiService.loginUser({ email, password });

      // If the response is successful, store user details and navigate
      if (response.statusCode === 200) {
        localStorage.setItem("token", response.token); // Save the user's JWT token
        localStorage.setItem("role", response.role); // Save the user's role (e.g., admin, user, etc.)
        navigate(from, { replace: true }); // Redirect to the page the user came from or fallback to "/home"
      }
    } catch (error) {
      // Handle any errors from the login request
      setError(error.response?.data?.message || error.message); // Show the server's error message or a generic one
      setTimeout(() => setError(""), 5000); // Clear the error after 5 seconds
    } finally {
      setLoading(false); // Set loading state to false after the request is complete
    }
  };

  return (
    <div className="auth-container">
      <h2>Login</h2>

      {/* Display error messages, if any */}
      {error && <p className="error-message">{error}</p>}

      {/* Conditionally render the spinner or the login form */}
      {loading ? (
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
      ) : (
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email: </label>
            <input
              type="email" // Input type is email for validation
              value={email} // Binds input value to the email state
              onChange={(e) => setEmail(e.target.value)} // Updates state when the user types
              required // Ensures the field is required
            />
          </div>
          <div className="form-group">
            <label>Password: </label>
            <input
              type="password" // Input type is password for masking characters
              value={password} // Binds input value to the password state
              onChange={(e) => setPassword(e.target.value)} // Updates state when the user types
              required // Ensures the field is required
            />
          </div>
          <button type="submit">Login</button>{" "}
          {/* Triggers the handleSubmit function */}
        </form>
      )}

      {/* Link to the registration page for users without an account */}
      <p className="register-link">
        Don't have an account? <a href="/register">Register</a>
      </p>
    </div>
  );
}

export default LoginPage;
