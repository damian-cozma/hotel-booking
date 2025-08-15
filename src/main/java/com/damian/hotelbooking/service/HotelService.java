package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.HotelDto;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.List;

public interface HotelService {

    void addHotel(HotelDto hotelDto, BindingResult bindingResult, Principal principal);
    List<HotelDto> listHotels();
}
