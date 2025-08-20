package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.BookingDto;
import org.springframework.validation.BindingResult;

public interface BookingService {
    void createBooking(BookingDto bookingDto, BindingResult bindingResult);

    void cancelBooking(Long bookingId);
}
