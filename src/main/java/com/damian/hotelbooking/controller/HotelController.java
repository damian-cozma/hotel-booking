package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.BookingDto;
import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Booking;
import com.damian.hotelbooking.entity.Room;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.repository.BookingRepository;
import com.damian.hotelbooking.repository.RoomRepository;
import com.damian.hotelbooking.service.*;
import org.h2.engine.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final AmenityService amenityService;

    public HotelController(HotelService hotelService,
                           AmenityService amenityService) {
        this.hotelService = hotelService;
        this.amenityService = amenityService;
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
