package com.paradisian.paradisianHotelMongo.security;


import com.paradisian.paradisianHotelMongo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;                              // A service that loads user-specific data during authentication. Injected using @Autowired
    @Autowired
    private JWTAuthFilter jwtAuthFilter;                                                    // A custom JWT authentication filter that validates JWT tokens on every request

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{ // This method configures the main security settings for HTTP requests
        httpSecurity.csrf(AbstractHttpConfigurer::disable)                                      // Disables Cross-Site Request Forgery (CSRF) protection. Using JWT for authentication (which is stateless) means CSRF protection is usually not needed
                .cors(Customizer.withDefaults())                                            // Enables Cross-Origin Resource Sharing (CORS) with default settings
                .authorizeHttpRequests(request-> request                                // Configures which endpoints are publicly accessible and which ones require authentication
                        .requestMatchers("/auth/**", "/rooms/**", "/bookings/**").permitAll()
                        .anyRequest().authenticated())                               // Requires authentication for all other endpoints not specified as publicly accessible
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   // Configures how sessions are handled
                .authenticationProvider(authenticationProvider())                                               // for user authentication
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);                           // Registers the jwtAuthFilter to process requests
return httpSecurity.build();                                                                                 // Builds and returns the SecurityFilterChain based on the configured settings.

    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);  // Retrieves user details for authentication
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());    // Specifies the password encoder to be used
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); //  Returns an instance of BCryptPasswordEncoder, which will be used to encode and match user passwords
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}


/*
Summary
Purpose: This class defines the security configuration for the application, including endpoint access rules, authentication, and session management.

Main Components:
SecurityFilterChain: Configures the HTTP security settings, such as CORS, CSRF, which endpoints are public, and session management.
JWT Filtering: Adds the JWTAuthFilter to handle JWT token validation.
AuthenticationProvider: Configures how the app retrieves user details and validates passwords during authentication.
Password Encoding: Uses BCryptPasswordEncoder to hash and compare passwords securely.
Authentication Management: Uses the AuthenticationManager to handle the process of authenticating users.

This configuration ensures that only authenticated users can access specific endpoints, while allowing public access to others, and handles JWT token-based authentication in a stateless manner.
 */