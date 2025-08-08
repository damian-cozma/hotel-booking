package com.damian.hotelbooking.service;

import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.dto.SignupRequest;
import java.util.List;


public interface UserService {
    List<User> findAll();
    User findById(Long theId);
    User save(User theEmployee);
    void deleteById(Long theId);
    User registerUser(SignupRequest signupRequest);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
