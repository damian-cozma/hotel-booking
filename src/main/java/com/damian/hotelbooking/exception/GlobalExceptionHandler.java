package com.damian.hotelbooking.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUsernameNotFoundException(UserNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "User not found: " + ex.getMessage());
        return "error/custom-error";
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public String handleRoomNotFoundException(RoomNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "Room not found: ID " + ex.getMessage());
        return "error/custom-error";
    }

    @ExceptionHandler(HotelNotFoundException.class)
    public String handleHotelNotFoundException(HotelNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "Hotel not found: ID " + ex.getMessage());
        return "error/custom-error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        model.addAttribute("errorMessage", "Access Denied: " + ex.getMessage());
        return "error/custom-error";
    }

}
