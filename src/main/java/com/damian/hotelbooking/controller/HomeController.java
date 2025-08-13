package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
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
