package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.BookingDto;
import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Booking;
import com.damian.hotelbooking.entity.Room;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.exception.BookingNotFoundException;
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
    private final UserService userService;
    private final RoomService roomService;
    private final BookingService bookingService;

    public HotelController(HotelService hotelService,
                           AmenityService amenityService,
                           UserService userService,
                           RoomService roomService,
                           BookingService bookingService) {
        this.hotelService = hotelService;
        this.amenityService = amenityService;
        this.userService = userService;
        this.roomService = roomService;
        this.bookingService = bookingService;
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
            @RequestParam(value = "capacity", defaultValue = "0") int capacity,
            @RequestParam(value = "roomType", required = false) String roomType,
            @RequestParam(value = "checkInDate", required = true) LocalDate checkInDate,
            @RequestParam(value = "checkOutDate", required = true) LocalDate checkOutDate,
            Model model) {

        model.addAttribute("hotels", hotelService.searchHotels(country, city, amenities, capacity, roomType, checkInDate, checkOutDate));
        model.addAttribute("allAmenities", amenityService.findAllAmenities());

        return "common/hotels/list";

    }

    @GetMapping("/{hotelId}/rooms/{roomId}/book")
    public String showCreateBookingForm(@PathVariable Long hotelId,
                                        @PathVariable Long roomId,
                                        Principal principal,
                                        Model model) {

        model.addAttribute("userId", userService.findByUsername(principal.getName()).getId());
        model.addAttribute("hotelId", hotelId);
        model.addAttribute("roomId", roomId);

        Room room = roomService.findById(roomId);
        model.addAttribute("roomPrice", room.getPrice());
        model.addAttribute("room", room);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setUserId(userService.findByUsername(principal.getName()).getId());
        bookingDto.setRoomId(roomId);

        model.addAttribute("booking", bookingDto);

        return "common/hotels/book";
    }

    @PostMapping("/{hotelId}/rooms/{roomId}/book")
    public String createBooking(@PathVariable Long hotelId,
                                @PathVariable Long roomId,
                                @ModelAttribute("booking") BookingDto bookingDto,
                                BindingResult bindingResult,
                                Principal principal,
                                Model model) {

        bookingService.createBooking(bookingDto, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", userService.findByUsername(principal.getName()).getId());
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("roomId", roomId);
            Room room = roomService.findById(roomId);
            model.addAttribute("roomPrice", room.getPrice());
            model.addAttribute("room", room);
            return "common/hotels/book";
        }

        return "redirect:/hotels/" + hotelId;
    }

}
