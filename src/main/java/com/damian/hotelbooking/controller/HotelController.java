package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.BookingDto;
import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Booking;
import com.damian.hotelbooking.entity.Room;
import com.damian.hotelbooking.repository.RoomRepository;
import com.damian.hotelbooking.service.*;
import org.h2.engine.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final AmenityService amenityService;
    private final UserService userService;
    private final BookingService bookingService;
    private final RoomService roomService;

    public HotelController(HotelService hotelService,
                           AmenityService amenityService,
                           UserService userService,
                           BookingService bookingService, RoomService roomService) {
        this.hotelService = hotelService;
        this.amenityService = amenityService;
        this.userService = userService;
        this.bookingService = bookingService;
        this.roomService = roomService;
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

    @GetMapping("/{hotelId}/rooms/{roomId}/book")
    public String showBookingForm(@PathVariable Long hotelId,
                                  @PathVariable Long roomId,
                                  Principal principal,
                                  Model model) {

        model.addAttribute("userId", userService.findByUsername(principal.getName()).getId());
        model.addAttribute("hotelId", hotelId);
        model.addAttribute("roomId", roomId);
        Room room = roomService.findById(roomId);
        model.addAttribute("roomPrice", room.getPrice());
        BookingDto bookingDto = new BookingDto();
        bookingDto.setUserId(userService.findByUsername(principal.getName()).getId());
        bookingDto.setRoomId(roomId);
        model.addAttribute("booking", bookingDto);

        return "common/hotels/book";

    }

    @PostMapping("/{hotelId}/rooms/{roomId}/book")
    public String booking(@PathVariable Long hotelId,
                          @PathVariable Long roomId,
                          @ModelAttribute("booking") BookingDto bookingDto,
                          BindingResult bindingResult,
                          Principal principal,
                          Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", userService.findByUsername(principal.getName()).getId());
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("roomId", roomId);
            Room room = roomService.findById(roomId);
            model.addAttribute("roomPrice", room.getPrice());

            return "common/hotels/book";
        }

        bookingService.createBooking(bookingDto, bindingResult);
        return "redirect:/hotels/" + hotelId;
    }
}
