package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.LoginRequest;
import com.damian.hotelbooking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String submitLoginForm(@Valid @ModelAttribute LoginRequest loginRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "login";
        }
        try {
            String token = authService.login(loginRequest);
            model.addAttribute("token", token);
            return "home";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }

}
