package com.damian.hotelbooking.repository;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.dto.RoomDto;
import com.damian.hotelbooking.entity.Hotel;
import com.damian.hotelbooking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.BindingResult;

public interface RoomRepository extends JpaRepository<Room, Long> {

}
