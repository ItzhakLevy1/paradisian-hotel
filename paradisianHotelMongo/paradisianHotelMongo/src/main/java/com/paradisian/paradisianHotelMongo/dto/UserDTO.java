package com.paradisian.paradisianHotelMongo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.paradisian.paradisianHotelMongo.entity.Booking;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private String id;
    private String email;
    private String name;
    private String phoneNumber;
    private String password;
    private String role;
    private List<BookingDTO> bookings = new ArrayList<>();
}
