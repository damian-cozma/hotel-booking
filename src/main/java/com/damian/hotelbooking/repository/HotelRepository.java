package com.damian.hotelbooking.repository;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findAllByOwnerId(Long ownerId);

}
