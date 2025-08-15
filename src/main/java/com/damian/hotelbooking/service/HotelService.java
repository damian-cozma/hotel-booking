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

    List<HotelDto> searchHotels(String city, String country, List<String> amenities);

    public HotelDto toHotelDto(Hotel hotel);

    public Hotel toHotel(HotelDto hotelDto);
}
