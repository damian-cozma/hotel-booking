package com.damian.hotelbooking.schedulingtasks;

import com.damian.hotelbooking.entity.BookingStatus;
import com.damian.hotelbooking.repository.BookingRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final BookingRepository bookingRepository;

    public ScheduledTasks(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @PostConstruct
    public void runOnStartup() {
        log.info("Running checkOuts on application startup");
        checkOuts();
    }

    @Scheduled(fixedRate = 3600000)
    public void checkOuts() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        bookingRepository.findAll().stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CHECKED_IN)
                .filter(booking -> {
                    LocalDate checkOutDate = booking.getCheckOutDate();
                    return checkOutDate.isBefore(today) ||
                            (checkOutDate.isEqual(today) && now.isBefore(LocalTime.NOON));
                })
                .forEach(booking -> {
                    booking.setStatus(BookingStatus.CHECKED_OUT);
                    bookingRepository.save(booking);
                    log.info("Booking with ID {} has been automatically checked out.", booking.getId());
                });
    }
}