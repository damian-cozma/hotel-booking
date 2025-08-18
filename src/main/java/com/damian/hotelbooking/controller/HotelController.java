package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.repository.RoomRepository;
import com.damian.hotelbooking.service.AmenityService;
import com.damian.hotelbooking.service.HotelService;
import com.damian.hotelbooking.service.RoomService;
import com.damian.hotelbooking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final AmenityService amenityService;
    private final RoomService roomService;
    private final RoomRepository roomRepository;

    public HotelController(HotelService hotelService, UserService userService, AmenityService amenityService, RoomService roomService, RoomRepository roomRepository) {
        this.hotelService = hotelService;
        this.amenityService = amenityService;
        this.roomService = roomService;
        this.roomRepository = roomRepository;
    }

    @GetMapping("/list")
    public String listHotels(Model model) {

        model.addAttribute("hotels", hotelService.listHotels());
        model.addAttribute("allAmenities", amenityService.findAllAmenities());
        return "common/hotels/list";

    }

    @GetMapping("/{hotelId}")
    public String showHotelDetails(@PathVariable("hotelId") Long hotelId, Model model) {

        HotelDto hotelDto = hotelService.findById(hotelId);
        model.addAttribute("hotel", hotelDto);
        model.addAttribute("rooms", hotelDto.getRooms());
        return "common/hotels/hotel-details";

    }

    @GetMapping("/search")
    public String searchHotels(
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "amenities", required = false) List<String> amenities,
            Model model) {

        model.addAttribute("hotels", hotelService.searchHotels(country, city, amenities));
        model.addAttribute("allAmenities", amenityService.findAllAmenities());

        return "common/hotels/list";

    }
}
