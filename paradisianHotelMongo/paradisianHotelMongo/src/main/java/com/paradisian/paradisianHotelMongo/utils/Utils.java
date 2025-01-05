package com.paradisian.paradisianHotelMongo.utils;


import com.paradisian.paradisianHotelMongo.dto.*;
import com.paradisian.paradisianHotelMongo.entity.*;

import java.security.SecureRandom;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateRandomConfirmationCode(int length) {   //  Generates a random alphanumeric string (confirmation code)
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC_STRING.length());
            char randomChar = ALPHANUMERIC_STRING.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }

    // Converts entities (which are your database objects) into UserDTO's (User Data Transfer Objects - which are simpler objects used for transferring data, typically to/from a client)
    public static UserDTO mapUserEntityToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();

        // Extracts key fields (id, name, email, phoneNumber, role) from the User entity and maps them to the corresponding fields in the UserDTO
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setRole(user.getRole());
        return userDTO;
    }

    public static RoomDTO mapRoomEntityToRoomDTO(Room room) {   //  Converts a Room entity to a RoomDTO object
        RoomDTO roomDTO = new RoomDTO();

        // Extracts fields like id, roomType, roomPrice, roomPhotoUrl, and roomDescription from the Room entity and maps them to the RoomDTO
        roomDTO.setId(room.getId());
        roomDTO.setRoomType(room.getRoomType());
        roomDTO.setRoomPrice(room.getRoomPrice());
        roomDTO.setRoomPhotoUrl(room.getRoomPhotoUrl());
        roomDTO.setRoomDescription(room.getRoomDescription());
        return roomDTO;
    }

    public static BookingDTO mapBookingEntityToBookingDTO(Booking booking) {    // Converts a Booking entity to a BookingDTO
        BookingDTO bookingDTO = new BookingDTO();

        // Extracts fields like checkInDate, checkOutDate, numOfAdults, numOfChildren, totalNumOfGuest, and bookingConfirmationCode from the Booking entity
        bookingDTO.setId(booking.getId());
        bookingDTO.setCheckInDate(booking.getCheckInDate());
        bookingDTO.setCheckOutDate(booking.getCheckOutDate());
        bookingDTO.setNumOfChildren(booking.getNumOfChildren());
        bookingDTO.setNumOfAdults(booking.getNumOfAdults());
        bookingDTO.setTotalNumOfGuest(booking.getTotalNumOfGuest());
        bookingDTO.setBookingConfirmationCode(booking.getBookingConfirmationCode());
        return bookingDTO;
    }

    public static RoomDTO mapRoomEntityToRoomDTOPlusBookings(Room room) {   // Converts a Room entity to a RoomDTO, but also includes the list of bookings associated with that room
        RoomDTO roomDTO = new RoomDTO();

        roomDTO.setId(room.getId());
        roomDTO.setRoomType(room.getRoomType());
        roomDTO.setRoomPrice(room.getRoomPrice());
        roomDTO.setRoomPhotoUrl(room.getRoomPhotoUrl());
        roomDTO.setRoomDescription(room.getRoomDescription());

        if (room.getBookings() != null) {
            roomDTO.setBookings(room.getBookings().stream().map(Utils::mapBookingEntityToBookingDTO).collect(Collectors.toList()));
        }

        return roomDTO;
    }

    public static BookingDTO mapBookingEntityToBookingDTOPlusBookedRooms(Booking booking, boolean mapUser) {    // Converts a Booking entity to a BookingDTO, including the associated Room and optionally the User who made the booking
        BookingDTO bookingDTO = new BookingDTO();

        bookingDTO.setId(booking.getId());
        bookingDTO.setCheckInDate(booking.getCheckInDate());
        bookingDTO.setCheckOutDate(booking.getCheckOutDate());
        bookingDTO.setNumOfChildren(booking.getNumOfChildren());
        bookingDTO.setNumOfAdults(booking.getNumOfAdults());
        bookingDTO.setTotalNumOfGuest(booking.getTotalNumOfGuest());
        bookingDTO.setBookingConfirmationCode(booking.getBookingConfirmationCode());

        if (mapUser) {  // If mapUser is true, the User associated with the booking is also converted into a UserDTO
            bookingDTO.setUser(Utils.mapUserEntityToUserDTO(booking.getUser()));
        }

        if (booking.getRoom() != null) {

            RoomDTO roomDTO = new RoomDTO();

            roomDTO.setId(booking.getRoom().getId());
            roomDTO.setRoomType(booking.getRoom().getRoomType());
            roomDTO.setRoomPrice(booking.getRoom().getRoomPrice());
            roomDTO.setRoomPhotoUrl(booking.getRoom().getRoomPhotoUrl());
            roomDTO.setRoomDescription(booking.getRoom().getRoomDescription());

            bookingDTO.setRoom(roomDTO);
        }

        return bookingDTO;
    }

    public static UserDTO mapUserEntityToUserDTOPlusUserBookingsAndRoom(User user) {    // Converts a User entity to a UserDTO, including all the bookings made by that user, and for each booking, the associated room
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setRole(user.getRole());

        if (!user.getBookings().isEmpty()) {
            userDTO.setBookings(user.getBookings().stream().map(booking -> mapBookingEntityToBookingDTOPlusBookedRooms(booking, false)).collect(Collectors.toList()));
        }
        return userDTO;
    }

    // Converts a list of User entities into a list of UserDTOs by mapping each user using the mapUserEntityToUserDTO method
    public static List<UserDTO> mapUserListEntityToUserListDTO(List<User> userList) {
        return userList.stream().map(Utils::mapUserEntityToUserDTO).collect(Collectors.toList());
    }

    // Converts a list of Room entities into a list of RoomDTOs using the mapRoomEntityToRoomDTO method
    public static List<RoomDTO> mapRoomListEntityToRoomListDTO(List<Room> roomList) {
        return roomList.stream().map(Utils::mapRoomEntityToRoomDTO).collect(Collectors.toList());
    }

    // Converts a list of Booking entities into a list of BookingDTOs using the mapBookingEntityToBookingDTO method
    public static List<BookingDTO> mapBookingListEntityToBookingListDTO(List<Booking> bookingList) {
        return bookingList.stream().map(Utils::mapBookingEntityToBookingDTO).collect(Collectors.toList());
    }

    /**
     * Validates the format of a phone number.
     * You can adjust the regex according to the specific format you require.
     *
     * @param phoneNumber the phone number to validate.
     * @return true if the phone number is valid, false otherwise.
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Example regex for a valid phone number (change as needed)
        String regex = "^[+]?\\d{1,4}?[-.\\s]?\\(?\\d{1,4}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

}


/*
Summary:

Random Confirmation Code Generation:
Generates a secure random alphanumeric string used as a booking confirmation code.

Entity-to-DTO Mapping Methods:
Converts entities (User, Room, Booking) from your database into simpler DTO objects for easier handling in other layers, such as API responses or services.

Bulk List Mapping:
Converts lists of entities into lists of DTOs.

These utility methods help streamline the conversion between the more complex entity objects (usually tied to the database)
and the simpler DTO objects used for communication between different application layers.
 */