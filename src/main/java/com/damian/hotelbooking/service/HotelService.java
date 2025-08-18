package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Hotel;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.List;

public interface HotelService {

    void addHotel(HotelDto hotelDto, BindingResult bindingResult, Principal principal);

    List<HotelDto> listHotels();

    HotelDto findById(Long hotelId);

    List<HotelDto> searchHotels(String country, String city, List<String> amenities);

    HotelDto toHotelDto(Hotel hotel);

    Hotel toHotel(HotelDto hotelDto);

    List<HotelDto> findAllByOwnerId(Principal principal);
}
