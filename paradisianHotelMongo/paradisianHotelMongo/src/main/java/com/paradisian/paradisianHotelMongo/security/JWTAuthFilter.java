package com.paradisian.paradisianHotelMongo.security;

import com.paradisian.paradisianHotelMongo.service.CustomUserDetailsService;
import com.paradisian.paradisianHotelMongo.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
/*import org.springframework.security.authentication.CachingUserDetailsService;*/
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JWTAuthFilter: This class intercepts incoming HTTP requests to validate JWT tokens and sets up the security context for authenticated users
@Component
public class JWTAuthFilter extends OncePerRequestFilter {


    // A utility class used for generating and validating JWT tokens
    @Autowired
    private JWTUtils jwtUtils;

    // A custom service that loads user details, caches user details for better performance during the authentication process
    @Autowired
   /* private CachingUserDetailsService cachingUserDetailsService; */
      private CustomUserDetailsService customUserDetailsService;

    // doFilterInternal: This class will be the first to intercept any request to the back end,
    // It contains the core logic to extract, validate, and set up authentication using JWT tokens.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");   // Extracts the Authorization header from the incoming request.
        final String jwtToken;
        final String userEmail;

        if (authHeader == null || authHeader.isBlank()) {   // Check if the Authorization Header is Present
            filterChain.doFilter(request, response);
            return;
        }
        jwtToken = authHeader.substring(7); // The token is extracted from the Authorization header, omitting the first 7 characters ("Bearer ")
        userEmail = jwtUtils.extractUserName(jwtToken); // The JWTUtils class is used to extract the username (email) from the token

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {  // Check if User is Not Authenticated
           /* UserDetails userDetails = cachingUserDetailsService.loadUserByUsername(userEmail);  // Load User Details by retrieving information about the user from the database */
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

            if(jwtUtils.isValidToken(jwtToken, userDetails)){   // Validate the JWT Token
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);
            }
        }
        filterChain.doFilter(request, response);    // Continue the Filter Chain
    }
}


/*
Summary:
This filter is the first line of defense in the application’s security.
It intercepts every request, extracts the JWT token from the Authorization header, validates it, and sets up the security context if the token is valid.

How it Works:
It looks for the Authorization header in the HTTP request.
If present, it extracts the JWT token and uses JWTUtils to validate the token and extract the user information.
It loads user details from the database using CachingUserDetailsService.
If the token is valid, it sets up the security context for the request.
Finally, it continues processing the request by passing it to the next filter in the chain.
*/