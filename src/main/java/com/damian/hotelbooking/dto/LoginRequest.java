package com.damian.hotelbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Field required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Field required")
    private String password;
}
