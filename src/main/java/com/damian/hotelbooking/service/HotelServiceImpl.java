package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Amenity;
import com.damian.hotelbooking.entity.Hotel;
import com.damian.hotelbooking.entity.Room;
import com.damian.hotelbooking.exception.HotelNotFoundException;
import com.damian.hotelbooking.exception.UserNotFoundException;
import com.damian.hotelbooking.repository.AmenityRepository;
import com.damian.hotelbooking.repository.HotelRepository;
import com.damian.hotelbooking.repository.UserRepository;
import org.springframework.cglib.core.Local;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.time.LocalDate;
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
    public void saveHotel(HotelDto hotelDto, BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            return;
        }

        Long currentUserId = null;
        if (principal != null) {
            currentUserId = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new UserNotFoundException(principal.getName()))
                    .getId();
            hotelDto.setOwnerId(currentUserId);
        }

        Hotel hotel;
        if (hotelDto.getId() != null) {
            hotel = hotelRepository.findById(hotelDto.getId())
                    .orElseThrow(() -> new HotelNotFoundException(hotelDto.getId().toString()));

            if (!hotel.getOwner().getId().equals(currentUserId)) {
                throw new AccessDeniedException("You are not the owner of this hotel");
            }

        } else {
            hotel = new Hotel();
            hotel.setRating(0.0);
        }

        hotel.setName(hotelDto.getName());
        hotel.setCity(hotelDto.getCity());
        hotel.setCountry(hotelDto.getCountry());
        hotel.setStreet(hotelDto.getStreet());
        hotel.setPostalCode(hotelDto.getPostalCode());
        hotel.setPhoneNumber(hotelDto.getPhoneNumber());
        hotel.setEmail(hotelDto.getEmail());
        hotel.setDescription(hotelDto.getDescription());
        hotel.setOwner(userService.findById(hotelDto.getOwnerId()));

        Set<String> amenities = hotelDto.getAmenities();
        if (amenities == null) amenities = Collections.emptySet();

        Set<Amenity> amenitySet = amenities.stream()
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
                .orElseThrow(() -> new HotelNotFoundException(hotelId.toString()));

        return toHotelDto(hotel);
    }

    @Override
    public List<HotelDto> searchHotels(String country, String city, List<String> amenities, int capacity,
                                       String roomType, LocalDate checkInDate, LocalDate checkOutDate) {
        return hotelRepository.findAll()
                .stream()
                .filter(hotel -> country == null || country.isBlank() || hotel.getCountry().equalsIgnoreCase(country))
                .filter(hotel -> city == null || city.isBlank() || hotel.getCity().equalsIgnoreCase(city))
                .filter(hotel -> {
                    if (amenities == null || amenities.isEmpty()) return true;
                    Set<String> hotelAmenities = hotel.getAmenities()
                            .stream()
                            .map(Amenity::getName)
                            .collect(Collectors.toSet());
                    return hotelAmenities.containsAll(amenities);
                })
                .map(hotel -> {
                    Set<Room> filteredRooms = hotel.getRooms().stream()
                            .filter(room -> room.getCapacity() >= capacity)
                            .filter(room -> roomType == null || roomType.isBlank() ||
                                    room.getType().name().equalsIgnoreCase(roomType))
                            .filter(room -> room.getBookings().stream().noneMatch(booking ->
                                    checkInDate.isBefore(booking.getCheckOutDate()) &&
                                            checkOutDate.isAfter(booking.getCheckInDate())
                            ))
                            .collect(Collectors.toSet());
                    hotel.setRooms(filteredRooms);
                    return hotel;
                })
                .filter(hotel -> !hotel.getRooms().isEmpty())
                .map(this::toHotelDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<HotelDto> findAllByOwnerId(Principal principal) {
        Long ownerId = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(principal.getName()))
                .getId();

        List<Hotel> hotels = hotelRepository.findAllByOwnerId(ownerId);
        return hotels.stream()
                .map(this::toHotelDto)
                .toList();
    }

    @Override
    public void checkOwnership(Long hotelId, Principal principal) {
        HotelDto hotelDto = findById(hotelId);
        if (!hotelDto.getOwnerId().equals(userService.findIdByUsername(principal.getName()))) {
            throw new AccessDeniedException("You are not the owner of this hotel");
        }
    }

    // Mappere HotelDto <-> Hotel

    @Override
    public HotelDto toHotelDto(Hotel hotel) {
        HotelDto hotelDto = new HotelDto();
        hotelDto.setOwnerId(hotel.getOwner().getId());
        hotelDto.setId(hotel.getId());
        hotelDto.setName(hotel.getName());
        hotelDto.setCountry(hotel.getCountry());
        hotelDto.setCity(hotel.getCity());
        hotelDto.setPostalCode(hotel.getPostalCode());
        hotelDto.setPhoneNumber(hotel.getPhoneNumber());
        hotelDto.setEmail(hotel.getEmail());
        hotelDto.setStreet(hotel.getStreet());
        hotelDto.setDescription(hotel.getDescription());
        hotelDto.setRating(hotel.getRating());
        Set<String> amenities = hotel.getAmenities().stream()
                .map(Amenity::getName)
                .collect(Collectors.toSet());
        hotelDto.setAmenities(amenities);
        hotelDto.setRooms(hotel.getRooms());

        if (hotel.getRooms() != null && !hotel.getRooms().isEmpty()) {
            hotelDto.setPricePerNight(
                    hotel.getRooms().stream()
                            .filter(Room::isAvailable)
                            .map(Room::getPrice)
                            .min(Double::compare)
                            .orElse(null)
            );
        }

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
