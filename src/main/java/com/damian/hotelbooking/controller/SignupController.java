package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.LoginRequest;
import com.damian.hotelbooking.dto.SignupRequest;
import com.damian.hotelbooking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SignupController {

    private final AuthService authService;

    public SignupController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String submitSignupForm(@Valid @ModelAttribute SignupRequest signupRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "/signup";
        }

        authService.register(signupRequest);
        return "redirect:/login";
    }

}
