package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.SignupDto;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("signupDto", new SignupDto());
        return "login/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("signupDto") SignupDto signupDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (userService.existsByEmail(signupDto.getEmail())) {
            bindingResult.rejectValue("email", "error.signupDto", "Email already registered");
        }

        if (userService.existsByUsername(signupDto.getUsername())) {
            bindingResult.rejectValue("username", "error.signupDto", "Username already taken");
        }

        if (userService.existsByPhoneNumber(signupDto.getPhoneNumber())) {
            bindingResult.rejectValue("phoneNumber", "error.signupDto", "Phone number already in use");
        }

        if (bindingResult.hasErrors()) {
            return "login/register";
        }

        try {
            User registeredUser = userService.registerUser(signupDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! You can now login with your username: " + registeredUser.getUsername());
            return "redirect:/login";
        } catch (Exception e) {
            bindingResult.rejectValue("", "error.signupDto", "Registration failed: " + e.getMessage());
            return "login/register";
        }
    }
}
