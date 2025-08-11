package com.damian.hotelbooking.service;

import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.dto.SignupDto;

import java.util.List;
import java.util.Optional;


public interface UserService {
    List<User> findAll();

    User findById(Long theId);

    User save(User theEmployee);

    void deleteById(Long theId);

    User registerUser(SignupDto signupDto);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    User saveWithPasswordEncoding(User user);

    boolean changePassword(User user, String currentPassword, String newPassword);
}
