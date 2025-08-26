package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.ProfileDto;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.dto.SignupDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.List;


public interface UserService {

    List<User> findAll();

    void registerUser(SignupDto signupDto, BindingResult bindingResult);

    void saveProfile(ProfileDto profileDto, Principal principal,
                     BindingResult bindingResult, Model model);

    User findById(Long theId);

    void save(User user);

    void changePassword(Principal principal, String currentPassword, String newPassword,
                        String confirmPassword, BindingResult bindingResult, Model model);

    void deleteAccount(Principal principal, HttpServletRequest request);

    User findByUsername(String name);

    ProfileDto getProfile(String name);

    void assignHotelOwner(Principal principal);

    void deleteById(Long userId);

    Long findIdByUsername(String name);
}
