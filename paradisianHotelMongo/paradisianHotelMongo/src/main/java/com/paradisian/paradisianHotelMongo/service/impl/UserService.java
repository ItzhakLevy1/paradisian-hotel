package com.paradisian.paradisianHotelMongo.service.impl;

import com.paradisian.paradisianHotelMongo.dto.LoginRequest;
import com.paradisian.paradisianHotelMongo.dto.Response;
import com.paradisian.paradisianHotelMongo.dto.UserDTO;
import com.paradisian.paradisianHotelMongo.entity.User;
import com.paradisian.paradisianHotelMongo.exception.OurException;
import com.paradisian.paradisianHotelMongo.repo.UserRepository;
import com.paradisian.paradisianHotelMongo.utils.JWTUtils;
import com.paradisian.paradisianHotelMongo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.paradisian.paradisianHotelMongo.service.interfac.*;

import java.util.List;

/**
 * Service class to manage user-related operations.
 * This class provides functionalities such as user registration, login, and management.
 */
@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Utility method to check if an email is already taken.
     *
     * @param email the email to check.
     * @return true if the email exists in the repository, false otherwise.
     */
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Response register(User user) {

        Response response = new Response();

        try {
            // Set default role if not provided
            if (user.getRole() == null || user.getRole().isBlank()) {
                user.setRole("USER");
            }

            // Check if email is already taken
            if (isEmailTaken(user.getId())) {
                throw new OurException("Email Already Exists");
            }

            // Encrypt the user's password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);

            // Map the saved user to a DTO
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);

            // Set response
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while registering a user: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {

        Response response = new Response();

        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // Fetch the user and generate a JWT token
            var user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new OurException("User Not Found"));
            var token = jwtUtils.generateToken(user);

            // Set response details
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("7 days");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while logging in user: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getAllUsers() {
        Response response = new Response();

        try {
            // Fetch all users and map them to DTOs
            List<User> userList = userRepository.findAll();
            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(userList);

            // Set response
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUserList(userDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while getting all users: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {

        Response response = new Response();

        try {
            // Fetch user by ID
            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User Not Found"));

            // Map user to DTO including booking details
            UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);

            // Set response
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while getting user booking history: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteUser(String userId) {

        Response response = new Response();

        try {
            // Check if user exists
            userRepository.findById(userId).orElseThrow(() -> new OurException("User Not Found"));

            // Delete the user
            userRepository.deleteById(userId);

            // Set response
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while deleting a user: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserById(String userId) {

        Response response = new Response();

        try {
            // Fetch user by ID
            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User Not Found"));

            // Map user to DTO
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);

            // Set response
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while getting a user by id: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {

        Response response = new Response();

        try {
            // Fetch user by email
            User user = userRepository.findByEmail(email).orElseThrow(() -> new OurException("User Not Found"));

            // Map user to DTO
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);

            // Set response
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while getting a user info: " + e.getMessage());
        }
        return response;
    }
}

/**
 * Summary:
 * The UserService class provides a service layer to manage user operations such as registration, login, fetching user details, and deletion.
 * The utility method isEmailTaken improves code reusability by centralizing the check for email existence.
 * Each method handles specific user-related operations, ensures proper exception handling, and returns a Response object with the result.
 */
