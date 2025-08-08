package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public String listUsers(Model model) {
        List<User> userList = userService.findAll();

        model.addAttribute("users", userList);

        return "users/list-users";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {
        User user = new User();

        model.addAttribute("user", user);

        return "users/user-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("userId") Long id, Model model) {
        User user = userService.findById(id);

        model.addAttribute("user", user);

        return "users/user-form";
    }

    @PostMapping("/submitForm")
    public String submitForm(@ModelAttribute("user") User user) {
        userService.saveWithPasswordEncoding(user);

        return "redirect:/users/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("userId") Long id) {
        userService.deleteById(id);

        return "redirect:/users/list";
    }

}
