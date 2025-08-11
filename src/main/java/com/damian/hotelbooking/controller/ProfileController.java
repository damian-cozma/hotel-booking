package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.PasswordDto;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

        if ("security".equals(section)) {
            model.addAttribute("passwordDto", new PasswordDto());
        }

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
    public String saveProfile(@ModelAttribute("user") User formUser, Principal principal,
                              RedirectAttributes redirectAttributes, BindingResult bindingResult,
                              Model model) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + principal.getName()));

        if (!user.getPhoneNumber().equals(formUser.getPhoneNumber()) &&
                userService.existsByPhoneNumber(formUser.getPhoneNumber())) {
            bindingResult.rejectValue("phoneNumber", "error.signupDto", "Phone number already in use");
        }

        if (!user.getEmail().equals(formUser.getEmail()) &&
                userService.existsByEmail(formUser.getEmail())) {
            bindingResult.rejectValue("email", "error.signupDto", "Email already registered");
        }

        if (bindingResult.hasErrors()) {
            formUser.setRole(user.getRole());
            model.addAttribute("user", formUser);
            model.addAttribute("editable", true);
            model.addAttribute("activeSection", "profile");
            return "profile/profile";
        }

        try {
            user.setFirstName(formUser.getFirstName());
            user.setLastName(formUser.getLastName());
            user.setEmail(formUser.getEmail());
            user.setPhoneNumber(formUser.getPhoneNumber());

            userService.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Successful!");
            return "redirect:/profile";
        } catch (Exception e) {
            bindingResult.rejectValue("", "error.signupDto", "Registration failed: " + e.getMessage());
            return "redirect:/profile";
        }

    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordDto") PasswordDto passwordDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Principal principal,
                                 Model model) {

        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.passwordDto", "New passwords don't match");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("activeSection", "security");
            return "profile/profile";
        }

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found - " + principal.getName()));

        boolean success = userService.changePassword(user, passwordDto.getCurrentPassword(), passwordDto.getNewPassword());

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Password changed successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
        }

        return "redirect:/profile?section=security";
    }

//    @PostMapping("/change-password")
//    public String changePassword(@RequestParam("currentPassword") String currentPassword,
//                                 @RequestParam("newPassword") String newPassword,
//                                 @RequestParam("confirmPassword") String confirmPassword, RedirectAttributes redirectAttributes, Principal principal) {
//
//        if (!newPassword.equals(confirmPassword)) {
//            redirectAttributes.addFlashAttribute("error", "New passwords don't match");
//            return "redirect:/profile?section=security";
//        }
//
//        User user = userService.findByUsername(principal.getName())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found - " + principal.getName()));
//
//        boolean success = userService.changePassword(user, currentPassword, newPassword);
//
//        if (success) {
//            redirectAttributes.addFlashAttribute("success", "Password changed successfully");
//        } else {
//            redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
//        }
//
//        return "redirect:/profile?section=security";
//    }

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
