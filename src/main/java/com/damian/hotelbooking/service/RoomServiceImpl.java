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
    private final RoomImageService roomImageService;

    public RoomServiceImpl(AmenityRepository amenityRepository,
                           RoomRepository roomRepository,
                           HotelRepository hotelRepository,
                           HotelService hotelService,
                           BookingRepository bookingRepository,
                           RoomImageService roomImageService) {
        this.amenityRepository = amenityRepository;
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.hotelService = hotelService;
        this.bookingRepository = bookingRepository;
        this.roomImageService = roomImageService;
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

        room.setCapacity(roomDto.getCapacity());
        room.setPrice(roomDto.getPrice());
        room.setType(roomDto.getType());
        room.setRoomNumber(roomDto.getRoomNumber());
        room.setDescription(roomDto.getDescription());
        room.setArea(roomDto.getArea());
        room.setFloor(roomDto.getFloor());

        List<String> amenities = roomDto.getAmenities();
        if (amenities == null) {
            amenities = Collections.emptyList();
        }

        List<Amenity> amenityList = amenities.stream()
                .filter(name -> !name.trim().isEmpty())
                .map(name -> amenityRepository.findByName(name)
                        .orElseGet(() -> {
                            Amenity newAmenity = new Amenity();
                            newAmenity.setName(name);
                            return amenityRepository.save(newAmenity);
                        }))
                .collect(Collectors.toList());

        room.setAmenities(amenityList);

        roomRepository.save(room);

        if (roomDto.getImages() != null && !roomDto.getImages().isEmpty()) {
            roomImageService.saveImages(roomDto.getImages(), room);
        }
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
