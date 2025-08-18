package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.RoomDto;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface RoomService {

    void addRoom(RoomDto roomDto, BindingResult bindingResult);

}
