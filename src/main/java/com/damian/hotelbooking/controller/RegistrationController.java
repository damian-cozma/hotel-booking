package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.SignupDto;
import com.damian.hotelbooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("signupDto", new SignupDto());
        return "auth/register";
    }

    @PostMapping
    public String processRegistration(@Valid @ModelAttribute("signupDto") SignupDto signupDto,
                               BindingResult bindingResult) {

        userService.registerUser(signupDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        return "redirect:/login";
    }
}
