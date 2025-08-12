package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/owner/hotels")
public class OwnerHotelController {

    private final HotelService hotelService;

    public OwnerHotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/add")
    public String showAddHotelForm(Model model) {
        model.addAttribute("hotelDto", new HotelDto());
        return "owner/hotel-form";
    }

    @PostMapping("/add")
    public String addHotel(@Valid @ModelAttribute("hotelDto") HotelDto hotelDto,
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
}
