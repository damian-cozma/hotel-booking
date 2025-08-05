package com.damian.hotelbooking.entity;

public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED;

    public String getBookingStatus() {
        return this.name();
    }
}
