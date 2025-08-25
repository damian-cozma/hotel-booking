package com.damian.hotelbooking.dto;

import com.damian.hotelbooking.entity.RoomType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {

    private Long id;

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotNull(message = "Room type is required")
    private RoomType type;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 50, message = "Capacity must be at most 50")
    private int capacity;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private double price;

    private Set<String> amenities;
}
