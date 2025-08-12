package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getAllUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/list-users";
    }

    @GetMapping("/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") User user) {
        userService.saveWithPasswordEncoding(user);
        return "redirect:/users";
    }

    @GetMapping("/{userId}/edit")
    public String showUpdateUserForm(@PathVariable("userId") Long userId, Model model) {
        User user = userService.findById(userId);
        model.addAttribute("user", user);
        return "admin/user-form";
    }

    @PutMapping("/{userId}")
    public String updateUser(@PathVariable("userId") Long userId, @ModelAttribute("user") User user) {
        user.setId(userId);
        userService.saveWithPasswordEncoding(user);
        return "redirect:/users";
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteById(userId);
        return "redirect:/users";
    }
}
