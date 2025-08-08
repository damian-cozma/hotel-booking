package com.damian.hotelbooking.service;

import com.damian.hotelbooking.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
    List<User> findAll();
    User findById(Long theId);
    User save(User theEmployee);
    void deleteById(Long theId);
}
