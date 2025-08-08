package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String viewProfile(Model model, Principal principal,
                              @RequestParam(value = "section", defaultValue = "profile") String section) {

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + principal.getName()));
        model.addAttribute("user", user);
        model.addAttribute("editable", false);
        model.addAttribute("activeSection", section);
        return "profile/profile";
    }

    @GetMapping("/edit")
    public String editProfile(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + principal.getName()));
        model.addAttribute("user", user);
        model.addAttribute("editable", true);
        model.addAttribute("activeSection", "profile");
        return "profile/profile";
    }

    @PostMapping("/edit")
    public String saveProfile(@ModelAttribute("user") User formUser, Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + principal.getName()));

        user.setFirstName(formUser.getFirstName());
        user.setLastName(formUser.getLastName());
        user.setEmail(formUser.getEmail());
        user.setPhoneNumber(formUser.getPhoneNumber());

        userService.save(user);

        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword, RedirectAttributes redirectAttributes, Principal principal) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords don't match");
            return "redirect:/profile?section=security";
        }

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found - " + principal.getName()));

        boolean success = userService.changePassword(user, currentPassword, newPassword);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Password changed successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
        }

        return "redirect:/profile?section=security";
    }

    @PostMapping("/delete-account")
    public String deleteAccount(Principal principal, HttpServletRequest request) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found - " + principal.getName()));

        Long userId = user.getId();

        userService.deleteById(userId);

        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }

    @GetMapping("/notifications")
    public String notifications() {
        return "profile/notifications";
    }

}
