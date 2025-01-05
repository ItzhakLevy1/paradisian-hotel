package com.paradisian.paradisianHotelMongo.repo;

import com.paradisian.paradisianHotelMongo.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByEmail(String email);  // Check for existing email address to prevent duplications in the database
    boolean existsByPhoneNumber(String phoneNumber);  // Check for existing phone numbers to prevent duplications in the database

    Optional<User> findByEmail(String email);
}
