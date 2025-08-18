package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.RoomDto;
import com.damian.hotelbooking.entity.Room;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface RoomService {

    void addRoom(RoomDto roomDto, BindingResult bindingResult);

    Room findById(Long roomId);
}
