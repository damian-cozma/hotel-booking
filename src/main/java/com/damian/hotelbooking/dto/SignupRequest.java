package com.damian.hotelbooking.dto;

import com.damian.hotelbooking.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank(message = "Field required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Field required")
    private String firstName;

    @NotBlank(message = "Field required")
    private String lastName;

    @NotBlank(message = "Field required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Field required")
    @Size(min = 6, message = "Password must contain at least 6 characters")
    private String password;

    @NotBlank(message = "Field required")
    private String phoneNumber;
}