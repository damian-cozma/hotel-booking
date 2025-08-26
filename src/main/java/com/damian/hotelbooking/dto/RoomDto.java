package com.damian.hotelbooking.dto;

import com.damian.hotelbooking.entity.RoomType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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

    @DecimalMin(value = "0.0", inclusive = false, message = "Area must be greater than 0")
    private double area;

    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    private String description;

    @Min(value = 0, message = "Floor must be 0 or higher")
    private int floor;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    private List<String> amenities;

    private List<MultipartFile> images;

    private List<String> imageUrls;
}