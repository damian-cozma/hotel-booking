package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Hotel;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.repository.HotelRepository;
import com.damian.hotelbooking.repository.UserRepository;
import com.damian.hotelbooking.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/owner/hotels")
public class OwnerHotelController {

    private final HotelService hotelService;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;

    public OwnerHotelController(HotelService hotelService, UserRepository userRepository, HotelRepository hotelRepository) {
        this.hotelService = hotelService;
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
    }

    @GetMapping("/new")
    public String showCreateHotelForm(Model model) {
        model.addAttribute("hotelDto", new HotelDto());
        return "owner/hotel-form";
    }

    @PostMapping("/new")
    public String createHotel(@Valid @ModelAttribute("hotelDto") HotelDto hotelDto,
                           BindingResult bindingResult,
                           Model model,
                           Principal principal) {

        if (bindingResult.hasErrors()) {
            System.out.println("Errors found: " + bindingResult.getAllErrors());
            return "owner/hotel-form";
        }

        hotelService.addHotel(hotelDto, bindingResult, principal);

        return "redirect:/";
    }

    @GetMapping("/list")
    public String listHotels(Model model, Principal principal) {

        model.addAttribute("hotels", hotelService.findAllByOwnerId(principal));
        return "owner/list";

    }

    @GetMapping("/edit/{hotelId}")
    public String showUpdateHotelForm(@PathVariable Long hotelId, Model model, Principal principal) {

        model.addAttribute("hotelDto", hotelService.findById(hotelId));
        return "owner/hotel-form";
    }

    @PutMapping("/{hotelId}/edit")
    public String updateHotel(@PathVariable Long hotelId,
                              @Valid @ModelAttribute("hotelDto") HotelDto hotelDto,
                              BindingResult bindingResult,
                              Principal principal) {
        if (bindingResult.hasErrors()) return "owner/hotel-form";
        hotelService.addHotel(hotelDto, bindingResult, principal);
        return "redirect:/owner/hotels/list";
    }

    @DeleteMapping("/{hotelId}")
    public String deleteHotel(@PathVariable("hotelId") Long userId) {
        hotelRepository.deleteById(userId);
        return "redirect:/owner/hotels/list";
    }
}
