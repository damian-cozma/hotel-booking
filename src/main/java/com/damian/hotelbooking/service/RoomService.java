package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.RoomDto;
import com.damian.hotelbooking.entity.Room;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.List;

public interface RoomService {

    void addRoom(Long hotelId, RoomDto roomDto, BindingResult bindingResult, Principal principal);

    Room findById(Long roomId);
}
