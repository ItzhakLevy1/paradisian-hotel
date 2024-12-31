package com.paradisian.paradisianHotelMongo.service.impl;

import com.paradisian.paradisianHotelMongo.dto.Response;
import com.paradisian.paradisianHotelMongo.dto.RoomDTO;
import com.paradisian.paradisianHotelMongo.entity.Booking;
import com.paradisian.paradisianHotelMongo.entity.Room;
import com.paradisian.paradisianHotelMongo.exception.OurException;
import com.paradisian.paradisianHotelMongo.repo.BookingRepository;
import com.paradisian.paradisianHotelMongo.repo.RoomRepository;
import com.paradisian.paradisianHotelMongo.service.AwsS3Service;
import com.paradisian.paradisianHotelMongo.service.interfac.IRoomService;
import com.paradisian.paradisianHotelMongo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// @Service Marks this class as a service component in the Spring Framework, meaning it's a service that can be injected into other components.
@Service
public class RoomService implements IRoomService {

    @Autowired
    private RoomRepository roomRepository;          // Handles database operations for Room entities (add, update, delete, find).

    @Autowired
    private BookingRepository bookingRepository;    // Handles operations for the Booking entity to retrieve booking-related data.

    @Autowired
    private AwsS3Service awsS3Service;              // This service is used for uploading room images to AWS S3 (cloud storage for images).


    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {

        Response response = new Response();

        try {
            String imageUrl = awsS3Service.saveImageToS3(photo);        // Uploads the room photo to AWS S3 using awsS3Service.
            Room room = new Room();                                     // Creates a new Room object And :
            room.setRoomPhotoUrl(imageUrl);                             // populates it with provided data (image URL), and saves it to the database.
            room.setRoomPrice(roomPrice);                               // populates it with provided data (price), and saves it to the database.
            room.setRoomType(roomType);                                 // populates it with provided data (type), and saves it to the database.
            room.setRoomDescription(description);                       // populates it with provided data (description), and saves it to the database.

            Room savedRoom = roomRepository.save(room);                 // saves the newly created Room entity to the database, The saved entity is assigned to savedRoom
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);  // converts the Room entity (savedRoom) into a RoomDTO using the Utils.mapRoomEntityToRoomDTO method

            response.setStatusCode(200);        // Ads a 200 Status Code to the response object
            response.setMessage("Successful");  // Ads a Successful message to the response object
            response.setRoom(roomDTO);          // Ads the converted Room entity to the response object

        } catch (Exception e) {                 // Handle exceptions
            response.setStatusCode(500);
            response.setMessage("Error occurred while saving a room: " + e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public Response getAllRooms() {

        Response response = new Response();     // Creates a new response object

        try {
            List<Room> roomList = roomRepository.findAll();                                 // Fetches all the rooms from the roomRepository and stores them in the roomList List
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);     // Maps all the rooms to RoomDTOs and stores them into the roomDTOList List

            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);      // Ads the roomDTOList List to the response object

        } catch (Exception e) {                     // Handle exceptions
            response.setStatusCode(500);
            response.setMessage("Error occurred while getting all rooms: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteRoom(String roomId) {

        Response response = new Response();     // Creates a new response object

        try {
            roomRepository.findById(roomId).orElseThrow(()-> new OurException("Room Not Found"));   // Finds a room by its ID
            roomRepository.deleteById(roomId );     // Deletes the room if it exists
            response.setStatusCode(200);            // Ads a 200 Status Code to the response object
            response.setMessage("Successful");      // Ads a Successful message to the response object

        } catch (OurException e) {                  // Handles a custom exceptions such as room being deleted is not found, and other specific, known conditions.
            response.setStatusCode(404);            // Indicates "Not Found"
            response.setMessage(e.getMessage());
        } catch (Exception e) {                     // Handles all other unexpected exceptions that could happen due to various reasons (e.g., database errors, connection issues etc.)
            response.setStatusCode(500);            // Indicates a server-side error
            response.setMessage("Error occurred while deleting a room: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateRoom(String roomId, String description, String roomtype, BigDecimal roomPrice, MultipartFile photo) {

        Response response = new Response();     // Creates a new response object

        try {
            String imageUrl = null;
            if (photo != null && !photo.isEmpty()){
                imageUrl = awsS3Service.saveImageToS3(photo);
            }

            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));  // Finds the room by roomId, throws OurException if not found

            if (roomtype != null) room.setRoomType(roomtype);                   // Updates any non-null properties (room type)
            if (roomPrice != null) room.setRoomPrice(roomPrice);                // Updates any non-null properties (room  price)
            if (description != null) room.setRoomDescription(description);      // Updates any non-null properties (room description)
            if (imageUrl != null) room.setRoomPhotoUrl(imageUrl);               // Updates any non-null properties (room photo)

            Room updatedRoom = roomRepository.save(room);                       /* Saves the updated room back to the database and stores it in updatedRoom which is an instance of a Room object,
                                                                                   If the room already exists (based on its ID), it updates the existing entry.
                                                                                   If it’s a new room (with no ID or a new ID), it creates a new record.*/
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);        // Maps it to RoomDTO


            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoom(roomDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating a room: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getRoomById(String roomId) {

        Response response = new Response();     // Creates a new response object

        try {
            Room room = roomRepository.findById(roomId).orElseThrow(()-> new OurException("Room Not Found"));   // Fetches the room by its ID, throws OurException if not found
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);   // Maps the room to a RoomDTO including its booking information

            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoom(roomDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while getting a room by id: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {

        Response response = new Response();     // Creates a new response object

        try {
            List<Booking> bookings = bookingRepository.findBookingsByDateRange(checkInDate, checkOutDate);      // Fetches all available rooms (those that are not booked), the result is stored in the bookings list
            List<String> bookedRoomsId = bookings.stream().map(booking -> booking.getRoom().getId()).toList();  // Maps the list of rooms that are already booked during the specified date range, the result is stored in the bookedRoomsId list

            List<Room> availableRooms = roomRepository.findByRoomTypeLikeAndIdNotIn(roomType, bookedRoomsId);   // Fetch Available Rooms,The result is stored in the availableRooms list
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(availableRooms);                   // Map Available Rooms to DTO,The result is stored in the roomDTOList list

            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while getting available rooms by date range and type: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllAvailableRooms() {

        Response response = new Response();     // Creates a new response object

        try {
            List<Room> roomList = roomRepository.findAllAvailableRooms();               // Fetches all available rooms (those that are not booked).
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList); // Maps the list of rooms to RoomDTO and adds them to the response

            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while getting all available rooms: " + e.getMessage());
        }
        return response;
    }
}


/*
Summary:
This class provides services for managing hotel room data,
including adding, updating, deleting, retrieving rooms, and fetching available rooms based on dates and room types.
It uses AWS S3 for photo storage, custom exception handling for error scenarios,
and utility methods for converting entities into DTOs for better encapsulation of data.
 */