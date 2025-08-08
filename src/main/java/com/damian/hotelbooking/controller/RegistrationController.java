package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.SignupRequest;
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
        model.addAttribute("signupRequest", new SignupRequest());
        return "/login/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("signupRequest") SignupRequest signupRequest,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "/login/register";
        }

        try {
            if (userService.existsByEmail(signupRequest.getEmail())) {
                bindingResult.rejectValue("email", "error.signupRequest", "Email already registered");
                return "/login/register";
            }

            if (userService.existsByUsername(signupRequest.getUsername())) {
                bindingResult.rejectValue("username", "error.signupRequest", "Username already taken");
                return "/login/register";
            }

            User registeredUser = userService.registerUser(signupRequest);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Registration successful! You can now login with your username: " + registeredUser.getUsername());
            
            return "redirect:/login";
            
        } catch (Exception e) {
            bindingResult.rejectValue("", "error.signupRequest", "Registration failed: " + e.getMessage());
            return "/login/register";
        }
    }
}
