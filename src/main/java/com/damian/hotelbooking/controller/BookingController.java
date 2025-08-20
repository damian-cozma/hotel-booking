package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.BookingDto;
import com.damian.hotelbooking.entity.Booking;
import com.damian.hotelbooking.entity.Room;
import com.damian.hotelbooking.exception.BookingNotFoundException;
import com.damian.hotelbooking.repository.BookingRepository;
import com.damian.hotelbooking.service.BookingService;
import com.damian.hotelbooking.service.RoomService;
import com.damian.hotelbooking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final UserService userService;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final RoomService roomService;

    public BookingController(UserService userService,
                             BookingService bookingService,
                             BookingRepository bookingRepository,
                             RoomService roomService) {
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    @GetMapping
    public String listBookings(Model model, Principal principal) {

        Long id = userService.findIdByUsername(principal.getName());
        List<Booking> bookings = bookingRepository.findByUserId(id);

        model.addAttribute("bookings", bookings);
        return "common/hotels/bookings";

    }

    @DeleteMapping("/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId,
                                Principal principal) {

        bookingService.cancelBooking(bookingId);

        return "redirect:/bookings";
    }

    @GetMapping("/{bookingId}")
    public String showBookingDetails(@PathVariable Long bookingId,
                                     Model model,
                                     Principal principal) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId.toString()));

        model.addAttribute("booking", booking);

        return "common/hotels/booking-details";
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
        List<LocalDate[]> unavailableDates = roomService.getUnavailableDateRanges(roomId);
        model.addAttribute("unavailableDates", unavailableDates);

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
            return "common/hotels/book";
        }

        return "redirect:/hotels/" + hotelId;
    }

}
