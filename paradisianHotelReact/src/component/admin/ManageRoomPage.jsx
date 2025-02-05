import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";
import Pagination from "../common/Pagination";
import RoomResult from "../common/RoomResult";
import "../../index.css";

const ManageRoomPage = () => {
  const [rooms, setRooms] = useState([]);
  const [filteredRooms, setFilteredRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [selectedRoomType, setSelectedRoomType] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [roomsPerPage] = useState(5);
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchRooms = async () => {
      try {
        const response = await ApiService.getAllRooms();
        const allRooms = response.roomList;
        const sortedRooms = sortRoomsByPrice(allRooms); // Sort rooms by price
        setRooms(sortedRooms);
        setFilteredRooms(sortedRooms);
      } catch (error) {
        console.error("Error fetching rooms:", error.message);
      } finally {
        setIsLoading(false);
      }
    };

    const fetchRoomTypes = async () => {
      try {
        const types = await ApiService.getRoomTypes();
        setRoomTypes(types);
      } catch (error) {
        console.error("Error fetching room types:", error.message);
      }
    };

    fetchRooms();
    fetchRoomTypes();
  }, []);

  const sortRoomsByPrice = (rooms) => {
    return rooms.sort((a, b) => b.roomPrice - a.roomPrice); // Sort rooms by price from high to low
  };

  const handleRoomTypeChange = (e) => {
    setSelectedRoomType(e.target.value);
    filterRooms(e.target.value);
  };

  const filterRooms = (type) => {
    if (type === "") {
      setFilteredRooms(rooms);
    } else {
      const filtered = rooms.filter((room) => room.roomType === type);
      setFilteredRooms(filtered);
    }
    setCurrentPage(1); // Reset to first page after filtering
  };

  // Pagination
  const indexOfLastRoom = currentPage * roomsPerPage;
  const indexOfFirstRoom = indexOfLastRoom - roomsPerPage;
  const currentRooms = filteredRooms.slice(indexOfFirstRoom, indexOfLastRoom);

  // Change page
  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  if (isLoading) {
    return (
      <div className="d-flex justify-content-center align-items-center spinner-container">
        <div className="large-spinner"></div>
      </div>
    );
  }

  return (
    <div className="all-rooms">
      <h2>All Rooms</h2>
      <div
        className="all-room-filter-div"
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <div className="filter-select-div">
          <label>Filter by Room Type:</label>
          <select value={selectedRoomType} onChange={handleRoomTypeChange}>
            <option value="">All</option>
            {roomTypes.map((type) => (
              <option key={type} value={type}>
                {type}
              </option>
            ))}
          </select>
          <button
            className="add-room-button"
            onClick={() => navigate("/admin/add-room")}
          >
            Add Room
          </button>
        </div>
      </div>

      <RoomResult roomSearchResults={currentRooms} />

      <Pagination
        roomsPerPage={roomsPerPage}
        totalRooms={filteredRooms.length}
        currentPage={currentPage}
        paginate={paginate}
      />
    </div>
  );
};

export default ManageRoomPage;
