package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.BookingDto;
import com.damian.hotelbooking.entity.Booking;
import com.damian.hotelbooking.entity.BookingStatus;
import com.damian.hotelbooking.entity.Room;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.exception.RoomNotFoundException;
import com.damian.hotelbooking.exception.UserNotFoundException;
import com.damian.hotelbooking.repository.BookingRepository;
import com.damian.hotelbooking.repository.RoomRepository;
import com.damian.hotelbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(UserRepository userRepository, RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void createBooking(BookingDto bookingDto, BindingResult bindingResult) {

        User user = userRepository.findById(bookingDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(bookingDto.getUserId().toString()));
        Room room = roomRepository.findById(bookingDto.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(bookingDto.getRoomId().toString()));

        long nights = java.time.temporal.ChronoUnit.DAYS.between(
                bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());

        double totalPrice = room.getPrice() * nights;

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckOutDate(bookingDto.getCheckOutDate());
        booking.setNumberOfGuests(bookingDto.getNumberOfGuests());
        booking.setStatus(BookingStatus.CONFIRMED);
        room.setAvailable(false);
        booking.setTotalPrice(totalPrice);

        bookingRepository.save(booking);

    }
}
