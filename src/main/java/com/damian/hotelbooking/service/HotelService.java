package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Hotel;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface HotelService {

    void saveHotel(HotelDto hotelDto, BindingResult bindingResult, Principal principal);

    List<HotelDto> listHotels();

    List<HotelDto> getTopBookedHotels();

    HotelDto findById(Long hotelId);

    List<HotelDto> searchHotels(String country, String city, String state, List<String> amenities, int capacity,
                                String roomType, LocalDate checkInDate, LocalDate checkOutDate);

    HotelDto toHotelDto(Hotel hotel);

    Hotel toHotel(HotelDto hotelDto);

    List<HotelDto> findAllByOwnerId(Principal principal);

    void checkOwnership(Long hotelId, Principal principal);
}
