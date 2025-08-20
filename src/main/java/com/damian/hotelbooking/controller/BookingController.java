package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.entity.Booking;
import com.damian.hotelbooking.exception.BookingNotFoundException;
import com.damian.hotelbooking.repository.BookingRepository;
import com.damian.hotelbooking.service.BookingService;
import com.damian.hotelbooking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final UserService userService;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    public BookingController(UserService userService,
                             BookingService bookingService,
                             BookingRepository bookingRepository) {
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
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

}
