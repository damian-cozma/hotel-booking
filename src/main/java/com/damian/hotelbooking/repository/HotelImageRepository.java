package com.damian.hotelbooking.repository;

import com.damian.hotelbooking.entity.HotelImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelImageRepository extends JpaRepository<HotelImage, Long> {

    List<HotelImage> findAllByHotelId(Long hotelId);

}
