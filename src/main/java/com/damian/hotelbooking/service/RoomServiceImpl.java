package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.dto.RoomDto;
import com.damian.hotelbooking.entity.Amenity;
import com.damian.hotelbooking.entity.Hotel;
import com.damian.hotelbooking.entity.Room;
import com.damian.hotelbooking.repository.AmenityRepository;
import com.damian.hotelbooking.repository.HotelRepository;
import com.damian.hotelbooking.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private final AmenityRepository amenityRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomServiceImpl(AmenityRepository amenityRepository,
                           RoomRepository roomRepository,
                           HotelRepository hotelRepository) {
        this.amenityRepository = amenityRepository;
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    @Transactional
    public void addRoom(RoomDto roomDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("Room errors: " + bindingResult.getAllErrors());
            return;
        }

        Room room = new Room();
        Hotel hotel = hotelRepository.findById(roomDto.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
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

}
