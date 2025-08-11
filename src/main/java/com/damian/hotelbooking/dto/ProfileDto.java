package com.damian.hotelbooking.dto;

import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfileDto {

    @NotBlank
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and underscores")
    private String username;

    @NotBlank
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "First name can only contain letters, spaces and hyphens")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Last name can only contain letters, spaces and hyphens")
    private String lastName;

    @NotBlank
    @Email(message = "Please provide a valid email address")
    @Size(min = 6, max = 100, message = "Email must be between 6 and 100 characters")
    private String email;

    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    @Pattern(regexp = "^\\+?[0-9]+$", message = "Phone number can only contain digits and optional + prefix")
    private String phoneNumber;

}
