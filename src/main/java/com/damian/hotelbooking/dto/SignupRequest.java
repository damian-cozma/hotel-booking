package com.damian.hotelbooking.dto;

import com.damian.hotelbooking.entity.UserRole;

public class SignupRequest {
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String phoneNumber;
    public UserRole role;
}