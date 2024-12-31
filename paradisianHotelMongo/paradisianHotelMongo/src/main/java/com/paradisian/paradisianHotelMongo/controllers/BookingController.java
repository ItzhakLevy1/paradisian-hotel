package com.paradisian.paradisianHotelMongo.controllers;


import com.paradisian.paradisianHotelMongo.dto.Response;
import com.paradisian.paradisianHotelMongo.entity.Booking;
import com.paradisian.paradisianHotelMongo.service.interfac.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController                 // Marks this class as a controller where each method returns a ResponseEntity (a wrapper around HTTP responses)
@RequestMapping("/bookings")    // Sets the base path for all the endpoints in this controller to /bookings. Each method's path will be relative to this base
public class BookingController {

    @Autowired                  // Injects the IBookingService service to use its methods within the controller
    private IBookingService bookingService;

    @PostMapping("/book-room/{roomId}/{userId}")                    // Defines an endpoint for POST requests at /book-room/{roomId}/{userId}. This allows the client to make a booking for a specific user in a specific room
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")  // This restricts access to only users or admins with the appropriate roles (ADMIN or USER)
    public ResponseEntity<Response> saveBooking(    // This method handles room booking requests. It allows an authenticated user or admin to book a room by providing roomId, userId, and booking details in the request body
            @PathVariable String roomId,            // The roomId is extracted from the URL path, representing the ID of the room being booked
            @PathVariable String userId,            // The userId is also extracted from the URL path, representing the ID of the user making the booking
            @RequestBody Booking bookingRequest) {  // The body of the POST request contains booking details (e.g., check-in, check-out dates). It is passed as a Booking object
        System.out.println("Save Booking was hit");
        Response response = bookingService.saveBooking(roomId, userId, bookingRequest);     // The booking request is passed to the bookingService.saveBooking(roomId, userId, bookingRequest) method for processing
        return ResponseEntity.status(response.getStatusCode()).body(response);              // A ResponseEntity is returned, wrapping the Response object that contains status codes and messages about the booking operation
    }

    @GetMapping("/all")                                 // Maps this method to handle GET requests to /bookings/all. It fetches all bookings in the system
    @PreAuthorize("hasAuthority('ADMIN')")              // Only administrators are allowed to access this endpoint. This ensures that regular users can't retrieve all bookings
    public ResponseEntity<Response> getAllBookings() {  // Retrieves all bookings from the system
        Response response = bookingService.getAllBookings();    // The bookingService.getAllBookings() method is called to fetch the list of all bookings
        return ResponseEntity.status(response.getStatusCode()).body(response);  // The results are wrapped in a ResponseEntity and returned with the appropriate status code

    }

    @GetMapping("/get-by-confirmation-code/{confirmationCode}")     // Maps the method to handle GET requests to /bookings/get-by-confirmation-code/{confirmationCode}. The confirmationCode is provided in the URL path
    public ResponseEntity<Response> getBookingsByConfirmationCode(@PathVariable String confirmationCode) {  // Retrieves booking details based on the confirmation code, The confirmation code is extracted from the URL and passed to the service layer to fetch the booking
        Response response = bookingService.findBookingByConfirmationCode(confirmationCode);     // The bookingService.findBookingByConfirmationCode(confirmationCode) method is invoked to retrieve the booking
        return ResponseEntity.status(response.getStatusCode()).body(response);      // The result is returned with the corresponding status code in the response
    }

    @DeleteMapping("/cancel/{bookingId}")   // This maps the method to handle DELETE requests to /bookings/cancel/{bookingId}. The bookingId is provided in the URL path
    @PreAuthorize("hasAuthority('ADMIN')")  // Only administrators are authorized to cancel bookings. This prevents regular users from deleting bookings
    public ResponseEntity<Response> cancelBooking(@PathVariable String bookingId) {     // Cancels an existing booking identified by the bookingId
        Response response = bookingService.cancelBooking(bookingId);    // The bookingService.cancelBooking(bookingId) method is called to cancel the booking and remove its association from both the user and room.
        return ResponseEntity.status(response.getStatusCode()).body(response);  // The response is wrapped in a ResponseEntity and returned with the appropriate status code and message
    }
}



/*
Summary
saveBooking: Allows a user or admin to book a room by sending a POST request with booking details.
getAllBookings: Admins can retrieve all bookings in the system via a GET request.
getBookingsByConfirmationCode: Retrieves a booking by its confirmation code using a GET request.
cancelBooking: Allows only admins to cancel a booking using a DELETE request with the booking ID.
Each method in the controller interacts with the IBookingService interface to perform business logic.
The controller layer simply handles HTTP requests, delegates business logic to the service layer, and returns a standardized response wrapped in a ResponseEntity.
 */