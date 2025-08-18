package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.service.HotelService;
import com.damian.hotelbooking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    private final UserService userService;
    private final HotelService hotelService;

    public HomeController(UserService userService, HotelService hotelService) {
        this.userService = userService;
        this.hotelService = hotelService;
    }

    @GetMapping("/")
    public String home() {
        return "common/home";
    }

    @GetMapping("/become-a-host")
    public String showBecomeAHostForm() {
        return "common/become-a-host";
    }

    @PostMapping("/become-a-host")
    public String becomeAHost(Principal principal) {

        userService.assignHotelOwner(principal);
        return "redirect:/owner/hotels/new";

    }

}
