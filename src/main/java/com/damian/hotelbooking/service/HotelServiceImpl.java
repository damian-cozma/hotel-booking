package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Amenity;
import com.damian.hotelbooking.entity.Hotel;
import com.damian.hotelbooking.repository.AmenityRepository;
import com.damian.hotelbooking.repository.HotelRepository;
import com.damian.hotelbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;

    public HotelServiceImpl(HotelRepository hotelRepository, UserService userService, UserRepository userRepository, AmenityRepository amenityRepository) {
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

        Set<Amenity> amenitySet = Arrays.stream(hotelDto.getAmenities().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(name -> {
                    return amenityRepository.findByName(name)
                            .orElseGet(() -> {
                                Amenity newAmenity = new Amenity();
                                newAmenity.setName(name);
                                return amenityRepository.save(newAmenity);
                            });
                })
                .collect(Collectors.toSet());

        hotel.setAmenities(amenitySet);

        hotelRepository.save(hotel);
    }

    @Override
    public List<HotelDto> listHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(hotel -> {
                    HotelDto dto = new HotelDto();
                    dto.setId(hotel.getId());
                    dto.setName(hotel.getName());
                    dto.setCity(hotel.getCity());
                    dto.setStreet(hotel.getStreet());
                    dto.setDescription(hotel.getDescription());
                    dto.setCountry(hotel.getCountry());
                    dto.setRating(hotel.getRating());

                    String amenities = hotel.getAmenities()
                            .stream()
                            .map(Amenity::getName)
                            .sorted()
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("");

                    dto.setAmenities(amenities);
                    return dto;
                })
                .toList();
    }
}
