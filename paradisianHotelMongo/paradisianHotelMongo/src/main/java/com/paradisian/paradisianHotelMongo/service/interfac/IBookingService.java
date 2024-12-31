package com.paradisian.paradisianHotelMongo.service.interfac;

import com.paradisian.paradisianHotelMongo.dto.Response;
import com.paradisian.paradisianHotelMongo.entity.Booking;

public interface IBookingService {

    Response saveBooking(String roomId, String userId, Booking BookingRequest);

    Response findBookingByConfirmationCode(String confirmationCode);

    Response getAllBookings();

    Response cancelBooking(String bookingId);
}
