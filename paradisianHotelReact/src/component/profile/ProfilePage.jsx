import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; // React Router hook for navigation
import ApiService from '../../service/ApiService'; // Service to handle API requests
import '../../../src/index.css';

const ProfilePage = () => {
    // State to store user data (profile details and booking history)
    const [user, setUser] = useState(null);

    // State to store error messages
    const [error, setError] = useState(null);

    const [isLoading, setIsLoading] = useState(false);

    const navigate = useNavigate(); // Hook to programmatically navigate between routes

    // Fetch user profile and booking history when the component mounts
    useEffect(() => {
        const fetchUserProfile = async () => {
            try {
                setIsLoading(true);
                // Fetch the user's basic profile details
                const response = await ApiService.getUserProfile();

                // Fetch user's booking history using their user ID
                const userPlusBookings = await ApiService.getUserBookings(response.user.id);

                // Combine user details with booking history and store in state
                setUser(userPlusBookings.user);
            } catch (error) {
                // Handle errors and display appropriate messages
                setError(error.response?.data?.message || error.message);
            } finally {
                setIsLoading(false);
            }
        };

        // Call the function to fetch user profile data
        fetchUserProfile();
    }, []); // Empty dependency array ensures this runs only once when the component is mounted

    // Handle user logout
    const handleLogout = () => {
        ApiService.logout(); // Clear session data or tokens
        navigate('/home'); // Redirect the user to the home page
    };

    // Navigate to the profile editing page
    const handleEditProfile = () => {
        navigate('/profile/edit'); // Redirect to the profile editing page
    };

    if (isLoading) {
        return (
          <div className="d-flex justify-content-center align-items-center spinner-container">
            <div className="large-spinner"></div>
          </div>
        );
      }
    return (
        <div className="profile-page">
            {/* Display a welcome message if user data is available */}
            {user && <h2>Welcome, {user.name}</h2>}

            <div className="profile-actions">
                {/* Button to navigate to the profile editing page */}
                <button className="edit-profile-button" onClick={handleEditProfile}>
                    Edit Profile
                </button>

                {/* Button to log out the user */}
                <button className="logout-button" onClick={handleLogout}>
                    Logout
                </button>
            </div>

            {/* Display an error message if there is an error */}
            {error && <p className="error-message">{error}</p>}

            {/* Display user profile details if available */}
            {user && (
                <div className="profile-details">
                    <h3>My Profile Details</h3>
                    <p><strong>Email:</strong> {user.email}</p>
                    <p><strong>Phone Number:</strong> {user.phoneNumber}</p>
                </div>
            )}

            {/* Section to display user's booking history */}
            <div className="bookings-section">
                <h3>My Booking History</h3>
                <div className="booking-list">
                    {/* Check if the user has bookings */}
                    {user && user.bookings.length > 0 ? (
                        user.bookings.map((booking) => (
                            // Render each booking as a card-like element
                            <div key={booking.id} className="booking-item">
                                <p><strong>Booking Code:</strong> {booking.bookingConfirmationCode}</p>
                                <p><strong>Check-in Date:</strong> {booking.checkInDate}</p>
                                <p><strong>Check-out Date:</strong> {booking.checkOutDate}</p>
                                <p><strong>Total Guests:</strong> {booking.totalNumOfGuest}</p>
                                <p><strong>Room Type:</strong> {booking.room.roomType}</p>
                                {/* Display the room photo */}
                                <img src={booking.room.roomPhotoUrl} alt="Room" className="room-photo" loading="lazy"/>
                            </div>
                        ))
                    ) : (
                        // Message if no bookings are found
                        <p>No bookings found.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;
