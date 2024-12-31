package com.paradisian.paradisianHotelMongo.controllers;

import com.paradisian.paradisianHotelMongo.dto.LoginRequest;
import com.paradisian.paradisianHotelMongo.dto.Response;
import com.paradisian.paradisianHotelMongo.entity.User;
import com.paradisian.paradisianHotelMongo.service.interfac.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// This annotation is a convenience annotation that combines @Controller and @ResponseBody.
// It indicates that this class will handle HTTP requests and return JSON or XML responses directly (no need to annotate each method with @ResponseBody)
@RestController


// This annotation specifies the base URL path for all methods in this controller. All endpoints will start with /auth
@RequestMapping("/auth")
public class AuthController {


    @Autowired  // This annotation is used for dependency injection, allowing Spring to automatically inject the IUserService implementation at runtime
    private IUserService userService;



    @PostMapping ("/register")  // This annotation indicates that this method will handle POST requests sent to /auth/register
    public ResponseEntity<Response> register(@RequestBody User user) {           // The User object is populated with data from the request body (typically JSON). Spring automatically converts the JSON into a User object.
        Response response = userService.register(user);                          // This method is expected to handle the registration logic
        return ResponseEntity.status(response.getStatusCode()).body(response);   // Ensures that the API response includes both the status and the message.
    }



    @PostMapping("/login")  // This method will handle POST requests sent to /auth/login
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest) {     // Takes a LoginRequest object populated from the JSON request body
        Response response = userService.login(loginRequest);                            // Handle the authentication logic, The result is stored in a Response object
        return  ResponseEntity.status(response.getStatusCode()).body(response);         // Returns a ResponseEntity with the appropriate HTTP status code and the response body
    }
}


/*
Summary
The AuthController class provides a clean and organized way to handle user registration and login requests in a Spring Boot application.
It uses the IUserService interface to delegate the actual logic for these operations, keeping the controller focused on handling HTTP requests and responses.
 */


/*
Suggestions to consider:

Error Handling: Consider adding error handling mechanisms (like @ControllerAdvice) to manage exceptions thrown during user registration or login.

Response Validation: Ensure that the User and LoginRequest objects are validated (e.g., using @Valid annotation) to enforce business rules.

Security: Consider implementing security measures, such as hashing passwords and managing session tokens or JWTs for logged-in users, to enhance the application's security posture.
 */