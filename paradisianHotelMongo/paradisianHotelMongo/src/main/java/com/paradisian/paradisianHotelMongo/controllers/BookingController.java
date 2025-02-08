package com.paradisian.paradisianHotelMongo.controllers;

import com.paradisian.paradisianHotelMongo.dto.Response;
import com.paradisian.paradisianHotelMongo.entity.Booking;
import com.paradisian.paradisianHotelMongo.service.interfac.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController                     // Marks this class as a controller where each method returns a ResponseEntity (a wrapper around HTTP responses)
public class BookingController {

    @Autowired  // Injects the booking service dependency
    private IBookingService bookingService;

    // Endpoint for room booking (accessible to ADMIN and USER)
    @PostMapping("/bookings/book-room/{roomId}/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Response> saveBooking(
            @PathVariable String roomId, // Room ID passed as a path variable
            @PathVariable String userId, // User ID passed as a path variable
            @RequestBody Booking bookingRequest) { // Booking details in the request body
        System.out.println("Save Booking was hit");
        Response response = bookingService.saveBooking(roomId, userId, bookingRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // ADMIN endpoint for managing bookings
    @GetMapping("/admin/manage-bookings")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllBookings() {
        Response response = bookingService.getAllBookings();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Endpoint to get booking details by confirmation code
    @GetMapping("/bookings/get-by-confirmation-code/{confirmationCode}")
    public ResponseEntity<Response> getBookingsByConfirmationCode(@PathVariable String confirmationCode) {
        Response response = bookingService.findBookingByConfirmationCode(confirmationCode);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Endpoint to cancel a booking (ADMIN only)
    @DeleteMapping("/bookings/cancel/{bookingId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> cancelBooking(@PathVariable String bookingId) {
        Response response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

/*
Summary
* BookingController handles all HTTP requests related to booking operations.
* It delegates the business logic to the IBookingService and returns the response in a standardized format.

saveBooking: Allows a user or admin to book a room by sending a POST request with booking details.
getAllBookings: Admins can retrieve all bookings in the system via a GET request.
getBookingsByConfirmationCode: Retrieves a booking by its confirmation code using a GET request.
cancelBooking: Allows only admins to cancel a booking using a DELETE request with the booking ID.
Each method in the controller interacts with the IBookingService interface to perform business logic.
The controller layer simply handles HTTP requests, delegates business logic to the service layer, and returns a standardized response wrapped in a ResponseEntity.
 */