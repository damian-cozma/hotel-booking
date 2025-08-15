package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Amenity;
import com.damian.hotelbooking.entity.Hotel;
import com.damian.hotelbooking.repository.AmenityRepository;
import com.damian.hotelbooking.repository.HotelRepository;
import com.damian.hotelbooking.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;

    public HotelServiceImpl(HotelRepository hotelRepository,
                            UserService userService,
                            UserRepository userRepository,
                            AmenityRepository amenityRepository) {
        this.hotelRepository = hotelRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.amenityRepository = amenityRepository;
    }

    @Override
    public void addHotel(HotelDto hotelDto, BindingResult bindingResult, Principal principal) {

        if (principal != null) {
            Long ownerId = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();
            hotelDto.setOwnerId(ownerId);
        }

        if (bindingResult.hasErrors()) {
            return;
        }

        if (hotelRepository.existsByEmail(hotelDto.getEmail())) {
            bindingResult.rejectValue("email", "error.hotelDto", "Email already registered");
            return;
        }

        if (hotelRepository.existsByPhoneNumber(hotelDto.getPhoneNumber())) {
            bindingResult.rejectValue("phoneNumber", "error.hotelDto", "Phone number already in use");
            return;
        }

        Hotel hotel = toHotel(hotelDto);
        Set<String> amenities = hotelDto.getAmenities();
        if (amenities == null) {
            amenities = Collections.emptySet();
        }

        Set<Amenity> amenitySet = amenities
                .stream()
                .filter(name -> !name.trim().isEmpty())
                .map(name -> amenityRepository.findByName(name).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        hotel.setAmenities(amenitySet);
        hotelRepository.save(hotel);
    }

    @Override
    public List<HotelDto> listHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(this::toHotelDto)
                .toList();
    }

    @Override
    public HotelDto findById(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new UsernameNotFoundException("Hotel not found"));

        return toHotelDto(hotel);
    }

    @Override
    public List<HotelDto> searchHotels(String city, String country, List<String> amenities) {
        return List.of();
    }

    // Mappere HotelDto <-> Hotel

    @Override
    public HotelDto toHotelDto(Hotel hotel) {
        HotelDto hotelDto = new HotelDto();
        hotelDto.setId(hotel.getId());
        hotelDto.setName(hotel.getName());
        hotelDto.setCountry(hotel.getCountry());
        hotelDto.setCity(hotel.getCity());
        hotelDto.setStreet(hotel.getStreet());
        hotelDto.setDescription(hotel.getDescription());
        hotelDto.setRating(hotel.getRating());
        Set<String> amenities = hotel.getAmenities().stream()
                .map(Amenity::getName)
                .collect(Collectors.toSet());
        hotelDto.setAmenities(amenities);

        return hotelDto;
    }

    @Override
    public Hotel toHotel(HotelDto hotelDto) {
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

        return hotel;
    }
}
