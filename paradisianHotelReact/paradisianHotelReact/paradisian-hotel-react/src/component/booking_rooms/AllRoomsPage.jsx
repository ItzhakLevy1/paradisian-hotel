import React, { useState, useEffect } from "react";
import ApiService from "../../service/ApiService";
import Pagination from "../common/Pagination";
import RoomResult from "../common/RoomResult";
import RoomSearch from "../common/RoomSearch";

// Component to display all rooms with filtering, searching, and pagination functionalities
const AllRoomsPage = () => {
  // State variables
  const [rooms, setRooms] = useState([]); // Stores all rooms fetched from the server
  const [filteredRooms, setFilteredRooms] = useState([]); // Stores rooms filtered by room type or search
  const [roomTypes, setRoomTypes] = useState([]); // Stores unique room types fetched from the server
  const [selectedRoomType, setSelectedRoomType] = useState(""); // Tracks the currently selected room type for filtering
  const [currentPage, setCurrentPage] = useState(1); // Tracks the current page for pagination
  const [roomsPerPage] = useState(5); // Number of rooms to display per page (fixed at 5)

  // Function to handle search results passed from the RoomSearch component
  const handleSearchResult = (results) => {
    setRooms(results); // Update the full rooms list with search results
    setFilteredRooms(results); // Update the filtered list with search results
  };

  // useEffect runs once when the component mounts to fetch data
  useEffect(() => {
    // Fetch all rooms from the server
    const fetchRooms = async () => {
      try {
        const response = await ApiService.getAllRooms(); // Call the API service to fetch all rooms
        const allRooms = response.roomList; // Extract the room list from the response
        setRooms(allRooms); // Update state with all rooms
        setFilteredRooms(allRooms); // Initially, filtered rooms are the same as all rooms
      } catch (error) {
        console.error("Error fetching rooms:", error.message); // Log any errors
      }
    };

    // Fetch unique room types from the server
    const fetchRoomTypes = async () => {
      try {
        const types = await ApiService.getRoomTypes(); // Call the API service to fetch room types
        setRoomTypes(types); // Update state with the list of unique room types
      } catch (error) {
        console.error("Error fetching room types:", error.message); // Log any errors
      }
    };

    fetchRooms(); // Fetch rooms on component mount
    fetchRoomTypes(); // Fetch room types on component mount
  }, []); // Empty dependency array ensures this runs only once when the component mounts

  // Event handler for room type selection
  const handleRoomTypeChange = (e) => {
    setSelectedRoomType(e.target.value); // Update the selected room type
    filterRooms(e.target.value); // Filter rooms based on the selected type
  };

  // Filter rooms by type
  const filterRooms = (type) => {
    if (type === "") {
      setFilteredRooms(rooms); // If no type is selected, reset to show all rooms
    } else {
      const filtered = rooms.filter((room) => room.roomType === type); // Filter rooms matching the selected type
      setFilteredRooms(filtered); // Update state with the filtered rooms
    }
    setCurrentPage(1); // Reset to the first page after filtering
  };

  // Pagination logic
  const indexOfLastRoom = currentPage * roomsPerPage; // Calculate the index of the last room on the current page
  const indexOfFirstRoom = indexOfLastRoom - roomsPerPage; // Calculate the index of the first room on the current page
  const currentRooms = filteredRooms.slice(indexOfFirstRoom, indexOfLastRoom); // Get the subset of rooms for the current page

  // Function to handle page changes
  const paginate = (pageNumber) => setCurrentPage(pageNumber); // Update the current page state

  return (
    <div className="all-rooms">
      <h2>All Rooms</h2>
      {/* Filter by Room Type Dropdown */}
      <div className="all-room-filter-div">
        <label>Filter by Room Type:</label>
        <select value={selectedRoomType} onChange={handleRoomTypeChange}>
          <option value="">All</option> {/* Default option to show all rooms */}
          {roomTypes.map((type) => (
            <option key={type} value={type}>
              {" "}
              {/* Render each room type as an option */}
              {type}
            </option>
          ))}
        </select>
      </div>
      {/* Room Search Component */}
      <RoomSearch handleSearchResult={handleSearchResult} />{" "}
      {/* Pass handleSearchResult to RoomSearch */}
      {/* Room Results Component */}
      <RoomResult roomSearchResults={currentRooms} />{" "}
      {/* Display the current page's rooms */}
      {/* Pagination Component */}
      <Pagination
        roomsPerPage={roomsPerPage} // Pass the number of rooms per page
        totalRooms={filteredRooms.length} // Total number of filtered rooms
        currentPage={currentPage} // Current page number
        paginate={paginate} // Function to change the page
      />
    </div>
  );
};

export default AllRoomsPage;
