package com.paradisian.paradisianHotelMongo.service.impl;



import com.paradisian.paradisianHotelMongo.dto.*;
import com.paradisian.paradisianHotelMongo.entity.*;
import com.paradisian.paradisianHotelMongo.exception.OurException;
import com.paradisian.paradisianHotelMongo.repo.*;
import com.paradisian.paradisianHotelMongo.service.interfac.IBookingService;
import com.paradisian.paradisianHotelMongo.service.interfac.IRoomService;
import com.paradisian.paradisianHotelMongo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service    // The @Service annotation marks this class as a Spring service component, indicating that it provides business logic.
public class BookingService implements IBookingService {
    // These fields are automatically injected by Spring through the @Autowired annotation.
    // They provide access to the necessary repositories for performing CRUD operations on the Booking, Room, and User entities
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private IRoomService roomService;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public Response saveBooking(String rooId, String userId, Booking bookingRequest) {                  // This method saves a new booking
        Response response = new Response();     // Creates a new response object

        try {
            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {           // checks if the check-out date is after the check-in date ( bookingRequest: The booking details )
                throw new IllegalArgumentException("Check in date must come before check out date");    // if It's not, it throws an IllegalArgumentException.
            }
            Room room = roomRepository.findById(rooId).orElseThrow(() -> new OurException("Room Not Found"));   // The ID of the room being booked
            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User Not Found"));  // The ID of the user making the booking

            List<Booking> existingBookings = room.getBookings();
            if (!roomIsAvailable(bookingRequest, existingBookings)) {                       // checks if the room is available for the requested dates using the roomIsAvailable method
                throw new OurException("Room not Available for the selected date range");
            }
            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);  // Generates a random booking confirmation code

            bookingRequest.setRoom(room);                                           // Sets the room on the booking request
            bookingRequest.setUser(user);                                          // Sets the user on the booking request
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);   // Sets the confirmation code on the booking request

            Booking savedBooking = bookingRepository.save(bookingRequest);      // Saves the booking to the database


            // Add the booking to the user's bookings list
            List<Booking> userBookings = user.getBookings();    // Retrieves the current list of bookings associated with the user.
            userBookings.add(savedBooking);                     // Adds the newly saved booking (savedBooking) to the user's bookings list
            user.setBookings(userBookings);                     // Updates the user's bookings list with the newly modified list that includes the new booking.
            userRepository.save(user);                          // Saves the updated user entity to the database


            // Add the booking to the room's bookings list
            List<Booking> roomBookings = room.getBookings();    // Retrieves the current list of bookings associated with the room
            roomBookings.add(savedBooking);                     // Adds the new booking to the room's bookings list
            room.setBookings(roomBookings);                     // Updates the room's bookings list to include the new booking
            roomRepository.save(room);                          // Saves the updated room entity to the database


            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingConfirmationCode(bookingConfirmationCode);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a  booking " + e.getMessage());
        }
        return response;
    }


    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {    // This method retrieves a booking using its confirmation code
        Response response = new Response();

        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new OurException("Booking Not Found"));   // Searches for a booking by the provided confirmation code
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRooms(booking, true);   // If found, it maps the booking entity to a BookingDTO for the response.
            response.setMessage("successful");
            response.setStatusCode(200);
            response.setBooking(bookingDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting booking by confirmation code " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllBookings() {      // This method retrieves all bookings from the database
        Response response = new Response();

        try {
            List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));  // Fetches all bookings and sorts them in descending order by ID.
            List<BookingDTO> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(bookingList);             // Maps the list of booking entities to a list of BookingDTOs
            response.setMessage("successful");
            response.setStatusCode(200);
            response.setBookingList(bookingDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all bookings " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response cancelBooking(String bookingId) {      // This method cancels a booking based on its ID
        Response response = new Response();

        try {
            Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new OurException("Booking Not Found"));   // Finds the booking using the provided ID

            // Remove the booking from the associated user
            User user = booking.getUser();
            if (user != null) {
                user.getBookings().removeIf(b -> b.getId().equals(bookingId));
                userRepository.save(user);
            }

            // Remove the booking from the associated room
            Room room = booking.getRoom();
            if (room != null) {
                room.getBookings().removeIf(b -> b.getId().equals(bookingId));
                roomRepository.save(room);
            }

            // Delete the booking from the repository
            bookingRepository.deleteById(bookingId);

            response.setMessage("successful");
            response.setStatusCode(200);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error cancelling a booking " + e.getMessage());
        }
        return response;
    }



    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {   // Checks whether the requested room is available for the specified date range.
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );

    }
}



/*
Summary
The BookingService class implements the core booking logic for a hotel management system.
It handles saving, retrieving, and canceling bookings while ensuring that data consistency is maintained across user and room records.
The use of exception handling provides clear feedback on operation outcomes, making it user-friendly and robust.
 */