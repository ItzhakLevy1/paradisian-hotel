package com.paradisian.paradisianHotelMongo.controllers;


import com.paradisian.paradisianHotelMongo.dto.Response;
import com.paradisian.paradisianHotelMongo.service.interfac.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController             // This annotation is used to mark the class as a Spring MVC controller where each method returns a ResponseEntity (typically JSON or XML). It combines @Controller and @ResponseBody
@RequestMapping("/rooms")   // Specifies that the base URL for all endpoints in this controller will start with /rooms
public class RoomController {
    @Autowired              // This injects the IRoomService interface, which provides the methods required to interact with room-related data
    private IRoomService roomService;


    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")          // Only users with the 'ADMIN' authority can perform this action
    public ResponseEntity<Response> addNewRoom (    // Adds a new room to the system. The method accepts room details such as a photo, room type, price, and description.
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "roomType", required = false) String roomType,
            @RequestParam(value = "roomPrice", required = false) BigDecimal roomPrice,
            @RequestParam(value = "roomDescription", required = false) String roomDescription
    ) {

        // Checks if required fields (photo, roomType, roomPrice) are provided. If not, it returns a 400 Bad Request response
        if (photo == null || photo.isEmpty() || roomType == null || roomType.isBlank() || roomPrice == null) {
            Response response = new Response();
            response.setStatusCode(400);
            response.setMessage("Please Provide values for all fields(photo, roomType, roomPrice)");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }

        // If valid data is provided, the roomService adds the room and returns a ResponseEntity with the appropriate HTTP status
        Response response = roomService.addNewRoom(photo, roomType, roomPrice, roomDescription);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")     // maps HTTP GET requests to the /rooms/all endpoint, when a client makes a GET request to /rooms/all, this method is executed.
    public ResponseEntity<Response> getAllRooms() {                             // Retrieves a list of all rooms
        Response response = roomService.getAllRooms();                          // Retrieves all roomS
        return ResponseEntity.status(response.getStatusCode()).body(response);  // Returns a list of rooms and an appropriate HTTP status
    }

    @GetMapping("/types")                       // Maps GET requests to /rooms/types. This allows clients to retrieve all available room types
    public List<String> getRoomTypes() {        // Retrieves a list of available room types
        return roomService.getAllRoomTypes();    // Retrieves all room types
    }

    @GetMapping("/room-by-id/{roomId}")     // Maps GET requests to /rooms/room-by-id/{roomId}, fetches the details of a specific room by its unique identifier (roomId)
    // The {roomId} in the URL is a placeholder. When a request is made (e.g., /rooms/room-by-id/123), roomId will be passed as a method argument
    public ResponseEntity<Response> getRoomByID(@PathVariable("roomId") String roomId) {
        Response response = roomService.getRoomById(roomId);                     // Retrieves room details by the room id
        return ResponseEntity.status(response.getStatusCode()).body(response);  // Response: Returns a ResponseEntity that includes the room details if the room is found or an error message if it is not
    }

    @GetMapping("/all-available-rooms")                             // Maps GET requests to /rooms/all-available-rooms. This endpoint retrieves a list of all rooms that are currently available for booking
    public ResponseEntity<Response> getAvailableRooms() {           // Interacts with the roomService to get only those rooms that are available
        Response response = roomService.getAllAvailableRooms();                 // Retrieves all of the available rooms
        return ResponseEntity.status(response.getStatusCode()).body(response);  // Returns a ResponseEntity with a list of available rooms
    }

    @GetMapping("/available-rooms-by-date-and-type")    // Maps GET requests to /rooms/available-rooms-by-date-and-type. fetches available rooms based on a date range (check-in and check-out) and a specific room type
    public ResponseEntity<Response> getAvailableRoomsByDateAndType(
            // CheckInDate and checkOutDate are optional query parameters, and they are formatted as ISO dates (e.g., 2024-10-04)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(required = false) String roomType     // roomType is also an optional parameter to filter by room type
    ) {
        if (checkInDate == null || checkOutDate == null || roomType.isBlank()) {
            Response response = new Response();
            response.setStatusCode(400);
            response.setMessage("All fields are required(checkInDate,checkOutDate,roomType )");
            return ResponseEntity.status(response.getStatusCode()).body(response);    // Returns a ResponseEntity with a status code
        }

        Response response = roomService.getAvailableRoomsByDateAndType(checkInDate, checkOutDate, roomType); // Retrieves the available rooms for the specified dates and room type from the roomService
        return ResponseEntity.status(response.getStatusCode()).body(response);   // Returns a ResponseEntity with the list of available rooms that match the criteria or an error if any required fields are missing
    }

    @PutMapping("/update/{roomId}")             // This annotation maps HTTP PUT requests to /rooms/update/{roomId}. The PUT method is typically used for updating resources, {roomId} is a path variable, meaning the room's unique identifier is provided as part of the URL (e.g., /rooms/update/12345)
    @PreAuthorize("hasAuthority('ADMIN')")      // Ensures that only users with the ADMIN role or authority can access this endpoint
    public ResponseEntity<Response> updateRoom(
            @PathVariable String roomId,        // Extracts the roomId from the URL. This is the ID of the room that needs to be updated
            @RequestParam(value = "photo", required = false) MultipartFile photo,   // Uses to accept an optional photo for the room. MultipartFile handles file uploads in Spring
            @RequestParam(value = "roomType", required = false) String roomType,    // The room type (e.g., "Single", "Double") can be updated. It's optional since it’s marked as required = false
            @RequestParam(value = "roomPrice", required = false) BigDecimal roomPrice,  // The room price can be updated. It's optional since it’s marked as required = false
            @RequestParam(value = "roomDescription", required = false) String roomDescription) {    // The room description can be updated. It's optional since it’s marked as required = false
        Response response = roomService.updateRoom(roomId, roomDescription, roomType, roomPrice, photo);    // Calls roomService.updateRoom to process the update. The service layer (roomService) will handle the actual updating of the room in the database
        return ResponseEntity.status(response.getStatusCode()).body(response);  // Returns a ResponseEntity, which is used to customize the HTTP response, including the status code
    }

    @DeleteMapping("/delete/{roomId}")          // This annotation maps HTTP DELETE requests to /rooms/delete/{roomId}. {roomId} in the URL represents the unique ID of the room to be deleted (e.g., /rooms/delete/12345)
    @PreAuthorize("hasAuthority('ADMIN')")      // Ensures that only users with the ADMIN role or authority can access this endpoint
    public ResponseEntity<Response> deleteRoom(@PathVariable String roomId) {   // Extracts the roomId from the URL. This is the ID of the room that needs to be deleted
        Response response = roomService.deleteRoom(roomId);                     // After attempting to delete the room, the method returns a Response object from roomService.deleteRoom(), which contains the result of the deletion (success or failure)
        return ResponseEntity.status(response.getStatusCode()).body(response);  // This response is wrapped in a ResponseEntity to control the HTTP status code that gets sent to the client
    }
}




/*
Summary of GET Endpoints:
/rooms/all: Retrieves a list of all rooms.
/rooms/types: Retrieves a list of all available room types.
/rooms/room-by-id/{roomId}: Retrieves the details of a specific room using its ID.
/rooms/all-available-rooms: Retrieves a list of all rooms that are currently available.
/rooms/available-rooms-by-date-and-type: Retrieves rooms based on availability for a specific date range and room type.
These annotations and methods are responsible for handling requests, gathering room data, and returning appropriate responses for each type of GET request related to room operations.
 */