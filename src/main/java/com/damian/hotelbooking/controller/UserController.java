package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.PasswordDto;
import com.damian.hotelbooking.dto.ProfileDto;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/account")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String viewProfile(Model model, Principal principal,
                              @RequestParam(value = "section", defaultValue = "profile") String section) {

        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("editable", false);
        model.addAttribute("activeSection", section);

        if ("security".equals(section)) {
            model.addAttribute("passwordDto", new PasswordDto());
        }

        return "common/account";
    }

    @GetMapping("/edit")
    public String editProfile(Model model, Principal principal) {
        ProfileDto profileDto = userService.getProfile(principal.getName());

        model.addAttribute("profileDto", profileDto);
        model.addAttribute("editable", true);
        model.addAttribute("activeSection", "profile");
        return "common/account";
    }

    @PutMapping("/edit")
    public String editProfile(@Valid @ModelAttribute("profileDto") ProfileDto profileDto,
                              BindingResult bindingResult, Principal principal, Model model) {

        userService.saveProfile(profileDto, principal, bindingResult, model);

        if (bindingResult.hasErrors()) {
            User user = userService.findByUsername(principal.getName());

            model.addAttribute("user", user);
            model.addAttribute("editable", true);
            model.addAttribute("activeSection", "profile");
            return "common/account";
        }
        return "redirect:/account?section=profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordDto") PasswordDto passwordDto,
                                 BindingResult bindingResult, Principal principal, Model model) {

        boolean success = userService.changePassword(principal, passwordDto.getCurrentPassword(),
                passwordDto.getNewPassword(), passwordDto.getConfirmPassword(), bindingResult, model);

        if (!success) {
            model.addAttribute("activeSection", "security");
            return "common/account";
        }

        return "redirect:/account?section=security";
    }

    @DeleteMapping("/delete")
    public String deleteAccount(Principal principal, HttpServletRequest request) {

        userService.deleteAccount(principal, request);

        return "redirect:/";
    }

    @GetMapping("/notifications")
    public String notifications() {
        return "common/notifications";
    }

}
