import React, { useState } from 'react';
import ApiService from '../../service/ApiService'; // Service to handle API requests
import { useNavigate } from 'react-router-dom'; // React Router hook for navigation

function RegisterPage() {
    const navigate = useNavigate(); // Hook to programmatically navigate to other routes

    // State to manage form input values
    const [formData, setFormData] = useState({
        name: '',       // User's name
        email: '',      // User's email
        password: '',   // User's password
        phoneNumber: '' // User's phone number
    });

    // State for error and success messages
    const [errorMessage, setErrorMessage] = useState(''); // Error message for validation or server issues
    const [successMessage, setSuccessMessage] = useState(''); // Success message upon successful registration

    // Handles input changes for the form
    const handleInputChange = (e) => {
        const { name, value } = e.target; // Get the name and value of the input field
        setFormData({ ...formData, [name]: value }); // Update the corresponding field in the formData state
    };

    // Validates the phone number format
    const isValidPhoneNumber = (phoneNumber) => {
        // Example regex for a valid phone number
        const regex = /^[+]?(\d{1,4}[-.\s]?)?(\(?\d{1,4}\)?[-.\s]?)?\d{1,4}[-.\s]?\d{1,4}[-.\s]?\d{1,4}$/;
        return regex.test(phoneNumber);
    };

    // Validates the form to ensure all fields are filled and phone number is valid
    const validateForm = () => {
        const { name, email, password, phoneNumber } = formData;
        if (!name || !email || !password || !phoneNumber) {
            return false; // Return false if any field is empty
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            setErrorMessage('Please enter a valid phone number.');
            return false; // Return false if phone number is invalid
        }
        
        return true; // Return true if all fields are valid
    };

    // Handles form submission for user registration
    const handleSubmit = async (e) => {
        e.preventDefault(); // Prevent default form submission (e.g., page reload)
        
        // Validate the form
        if (!validateForm()) {
            setTimeout(() => setErrorMessage(''), 5000); // Clear error after 5 seconds
            return;
        }

        try {
            // Call the `registerUser` method from ApiService to send form data to the server
            const response = await ApiService.registerUser(formData);

            // Check if the registration is successful
            if (response.statusCode === 200) {
                // Clear form fields
                setFormData({
                    name: '',
                    email: '',
                    password: '',
                    phoneNumber: ''
                });

                // Display success message
                setSuccessMessage('User registered successfully');
                
                // Redirect to the login page after 3 seconds
                setTimeout(() => {
                    setSuccessMessage('');
                    navigate('/login'); // Navigate to the login page
                }, 3000);
            }
        } catch (error) {
            // Check for specific error like "Email is already taken"
            if (error.response && error.response.data && error.response.data.message) {
                setErrorMessage(error.response.data.message); // Show the error message from the backend
            } else {
                setErrorMessage(error.message || "An error occurred"); // Show generic error message
            }
            setTimeout(() => setErrorMessage(''), 5000); // Clear error after 5 seconds
        }
    };

    return (
        <div className="auth-container">
            {/* Show error message, if any */}
            {errorMessage && <p className="error-message">{errorMessage}</p>}
            
            {/* Show success message, if any */}
            {successMessage && <p className="success-message">{successMessage}</p>}
            
            <h2>Sign Up</h2>
            
            {/* Registration form */}
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Name:</label>
                    {/* Input field for the user's name */}
                    <input 
                        type="text" 
                        name="name" 
                        value={formData.name} 
                        onChange={handleInputChange} 
                        required 
                    />
                </div>
                <div className="form-group">
                    <label>Email:</label>
                    {/* Input field for the user's email */}
                    <input 
                        type="email" 
                        name="email" 
                        value={formData.email} 
                        onChange={handleInputChange} 
                        required 
                    />
                </div>
                <div className="form-group">
                    <label>Phone Number:</label>
                    {/* Input field for the user's phone number */}
                    <input 
                        type="text" 
                        name="phoneNumber" 
                        value={formData.phoneNumber} 
                        onChange={handleInputChange} 
                        required 
                    />
                </div>
                <div className="form-group">
                    <label>Password:</label>
                    {/* Input field for the user's password */}
                    <input 
                        type="password" 
                        name="password" 
                        value={formData.password} 
                        onChange={handleInputChange} 
                        required 
                    />
                </div>
                {/* Submit button */}
                <button type="submit">Register</button>
            </form>
            
            {/* Link to the login page for users who already have an account */}
            <p className="register-link">
                Already have an account? <a href="/login">Login</a>
            </p>
        </div>
    );
}

export default RegisterPage;
