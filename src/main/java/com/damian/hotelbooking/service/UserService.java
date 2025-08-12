package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.ProfileDto;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.dto.SignupDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.naming.Binding;
import java.security.Principal;
import java.util.List;
import java.util.Optional;


public interface UserService {
    List<User> findAll();

    User registerUser(SignupDto signupDto, BindingResult bindingResult);

    User saveProfile(ProfileDto profileDto, Principal principal,
                     BindingResult bindingResult, Model model);

    User findById(Long theId);


    User saveWithPasswordEncoding(User user);

    boolean changePassword(Principal principal, String currentPassword, String newPassword,
                           String confirmPassword, BindingResult bindingResult, Model model);

    void deleteAccount(Principal principal, HttpServletRequest request);

    User findByUsername(String name);

    void deleteById(Long id);

    ProfileDto getProfile(String name);
}
