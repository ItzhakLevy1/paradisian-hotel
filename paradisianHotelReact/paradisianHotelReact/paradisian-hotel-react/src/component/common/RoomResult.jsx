import React from "react"; // Importing React to create the component
import { useNavigate } from "react-router-dom"; // Importing useNavigate for programmatic navigation
import ApiService from "../../service/ApiService"; // Importing ApiService to check user role (admin or not)

const RoomResult = ({ roomSearchResults }) => { // Collecting the props value = roomSearchResults ( which is an array of room details )
  const navigate = useNavigate(); // Initialize the `useNavigate` hook for navigation
  const isAdmin = ApiService.isAdmin();

  return (
    <section className="room-results">
      {roomSearchResults && roomSearchResults.length > 0 && (  // Check if there are any search results 
        <div className="room-list">
          {roomSearchResults.map((room) => ( // Map over the search results to render each room 
            <div key={room.id} className="room-list-item">
              <img
                className="room-list-item-image"
                src={room.roomPhotoUrl}
                alt={room.roomType}
              />
              <div className="room-details">  {/* Display room details */}
                <h3>{room.roomType}</h3>
                <p>Price: ${room.roomPrice} / night</p>
                <p>Description: {room.roomDescription}</p>
              </div>

              <div className="book-now-div">
                {isAdmin ? (   // Conditionally render buttons based on user role 
                  <button
                    className="edit-room-button"
                    onClick={() => navigate(`/admin/edit-room/${room.id}`)} // Navigate to edit room with room ID
                  >
                    Edit Room
                  </button>
                ) : (
                  <button
                    className="book-now-button"
                    onClick={() => navigate(`/room-details-book/${room.id}`)} // Navigate to book a room with a room ID
                  >
                    View/Book Now
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
};

export default RoomResult;
