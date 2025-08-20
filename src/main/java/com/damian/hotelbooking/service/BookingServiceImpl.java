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

import java.util.List;

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

        Long ownerId = room.getHotel().getOwner().getId();

        long nights = java.time.temporal.ChronoUnit.DAYS.between(bookingDto.getCheckInDate(),
                bookingDto.getCheckOutDate());

        if (nights > 30) {
            bindingResult.rejectValue("checkOutDate", "booking.tooLong", "Cannot book for more than 30 days.");
        }

        if (bookingDto.getUserId().equals(ownerId)) {
            bindingResult.reject("owner.booking", "Owners can't book their own listings.");
        }

        if (bookingDto.getCheckInDate().isAfter(bookingDto.getCheckOutDate())) {
            bindingResult.rejectValue("checkOutDate", "booking.illegalCheckInOut", "Cannot check in after check out.");
        }

        if (bookingDto.getCheckInDate().isEqual(bookingDto.getCheckOutDate())) {
            bindingResult.rejectValue("checkOutDate", "booking.oneNight", "Must book at least one night.");
        }

        if (bookingDto.getNumberOfGuests() > room.getCapacity()) {
            bindingResult.rejectValue("numberOfGuests", "booking.capacity", "Capacity reached.");
        }

        List<Booking> existingBookings = bookingRepository.findByRoomId(room.getId());
        boolean overlaps = existingBookings.stream().anyMatch(b ->
                !bookingDto.getCheckOutDate().isBefore(b.getCheckInDate()) && !bookingDto.getCheckInDate().isAfter(b.getCheckOutDate())
        );
        if (overlaps) {
            bindingResult.rejectValue("checkInDate", "booking.overlap", "Selected dates overlap with an existing booking.");
        }

        if (bindingResult.hasErrors()) {
            return;
        }

        double totalPrice = room.getPrice() * nights;

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckOutDate(bookingDto.getCheckOutDate());
        booking.setNumberOfGuests(bookingDto.getNumberOfGuests());
        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setTotalPrice(totalPrice);

        bookingRepository.save(booking);

    }

    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new RoomNotFoundException(bookingId.toString())
        );

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
}
