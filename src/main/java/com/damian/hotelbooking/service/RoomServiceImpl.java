package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.dto.RoomDto;
import com.damian.hotelbooking.entity.*;
import com.damian.hotelbooking.exception.HotelNotFoundException;
import com.damian.hotelbooking.exception.RoomNotFoundException;
import com.damian.hotelbooking.repository.AmenityRepository;
import com.damian.hotelbooking.repository.BookingRepository;
import com.damian.hotelbooking.repository.HotelRepository;
import com.damian.hotelbooking.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private final AmenityRepository amenityRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final HotelService hotelService;
    private final BookingRepository bookingRepository;

    public RoomServiceImpl(AmenityRepository amenityRepository,
                           RoomRepository roomRepository,
                           HotelRepository hotelRepository,
                           HotelService hotelService, BookingRepository bookingRepository) {
        this.amenityRepository = amenityRepository;
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.hotelService = hotelService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public void addRoom(Long hotelId, RoomDto roomDto, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) return;

        roomDto.setHotelId(hotelId);

        hotelService.checkOwnership(hotelId, principal);

        Room room = new Room();
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId.toString()));
        room.setHotel(hotel);

        room.setDescription(roomDto.getDescription());
        room.setCapacity(roomDto.getCapacity());
        room.setPrice(roomDto.getPrice());
        room.setType(roomDto.getType());
        room.setAvailable(roomDto.isAvailable());

        Set<Amenity> amenitySet = Collections.emptySet();
        if (roomDto.getAmenities() != null && !roomDto.getAmenities().isBlank()) {
            amenitySet = Arrays.stream(roomDto.getAmenities().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(name -> amenityRepository.findByName(name).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        room.setAmenities(amenitySet);

        roomRepository.save(room);
    }

    @Override
    public List<LocalDate[]> getUnavailableDateRanges(Long roomId) {
        List<Booking> bookings = bookingRepository.findByRoomId(roomId);
        return bookings.stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .map(b -> new LocalDate[]{b.getCheckInDate(), b.getCheckOutDate()})
                .toList();
    }


    @Override
    public Room findById(Long roomId) {

        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId.toString()));

    }

}
