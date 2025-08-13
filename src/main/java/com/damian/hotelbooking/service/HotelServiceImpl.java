package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Hotel;
import com.damian.hotelbooking.repository.HotelRepository;
import com.damian.hotelbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.security.Principal;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public HotelServiceImpl(HotelRepository hotelRepository, UserService userService, UserRepository userRepository) {
        this.hotelRepository = hotelRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void addHotel(HotelDto hotelDto, BindingResult bindingResult, Principal principal) {

        if (principal != null) {
            Long ownerId = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();
            hotelDto.setOwnerId(ownerId);
        }

        if (hotelRepository.existsByEmail(hotelDto.getEmail())) {
            bindingResult.rejectValue("email", "error.hotelDto", "Email already registered");
        }

        if (hotelRepository.existsByPhoneNumber(hotelDto.getPhoneNumber())) {
            bindingResult.rejectValue("phoneNumber", "error.hotelDto", "Phone number already in use");
        }

        Hotel hotel = new Hotel();
        hotel.setName(hotelDto.getName());
        hotel.setCity(hotelDto.getCity());
        hotel.setCountry(hotelDto.getCountry());
        hotel.setStreet(hotelDto.getStreet());
        hotel.setPostalCode(hotelDto.getPostalCode());
        hotel.setPhoneNumber(hotelDto.getPhoneNumber());
        hotel.setEmail(hotelDto.getEmail());
        hotel.setDescription(hotelDto.getDescription());
        hotel.setOwner(userService.findById(hotelDto.getOwnerId()));
        hotel.setRating(0.0);

        hotelRepository.save(hotel);
    }
}
