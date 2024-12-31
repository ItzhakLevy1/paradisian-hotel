package com.paradisian.paradisianHotelMongo.repo;

import com.paradisian.paradisianHotelMongo.entity.Room;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RoomRepository extends MongoRepository<Room, String> {

    @Aggregation("{ $group: {_id: '$roomType'} }")   // get all room types base on their uniqueness
    List<String> findDistinctRoomTypes();

    @Query("{'bookings': {$size: 0 }}") // find a room that has no bookings
    List<Room> findAllAvailableRooms();

    List<Room> findByRoomTypeLikeAndIdNotIn(String roomType, List<String> bookedRoomsIds);
}
