package com.paradisian.paradisianHotelMongo.service.interfac;


import com.paradisian.paradisianHotelMongo.dto.LoginRequest;
import com.paradisian.paradisianHotelMongo.dto.Response;
import com.paradisian.paradisianHotelMongo.entity.User;

public interface IUserService {

    Response updateUserProfile(User updatedUser);

    Response updateUserProfile(User updatedUser, String authenticatedUserEmail);

    boolean isEmailTaken(String email);

    Response register(User user);

    Response login(LoginRequest loginRequest);

    Response getAllUsers();

    Response getUserBookingHistory(String userId);

    Response deleteUser(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);
}
