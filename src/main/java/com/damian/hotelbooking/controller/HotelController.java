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
@RequestMapping("/hotel")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/submitForm")
    public String showHotelForm(Model model) {
        model.addAttribute("hotelDto", new HotelDto());
        return "hotels/hotel-form";
    }

    @PostMapping("/submitForm")
    public String addHotel(@Valid @ModelAttribute("hotelDto") HotelDto hotelDto,
                           BindingResult bindingResult,
                           Model model,
                           Principal principal) {

        if (bindingResult.hasErrors()) {
            System.out.println("Errors found: " + bindingResult.getAllErrors());
            return "hotels/hotel-form";
        }

        hotelService.addHotel(hotelDto, bindingResult, principal);

        return "redirect:/";
    }
}
