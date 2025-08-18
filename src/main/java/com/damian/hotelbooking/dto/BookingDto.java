package com.damian.hotelbooking.dto;

import com.damian.hotelbooking.entity.BookingStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "Number of guests must be at least 1")
    @Max(value = 20, message = "Number of guests must be at most 20")
    private int numberOfGuests;

    @NotNull(message = "Booking status is required")
    private BookingStatus status;

    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    private double totalPrice;
}