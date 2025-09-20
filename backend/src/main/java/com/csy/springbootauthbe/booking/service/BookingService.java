package com.csy.springbootauthbe.booking.service;

import com.csy.springbootauthbe.booking.dto.BookingDTO;
import com.csy.springbootauthbe.booking.dto.BookingRequest;

import java.util.List;

public interface BookingService {
    BookingDTO createBooking(BookingRequest dto);
    List<BookingDTO> getBookingsForTutor(String tutorId, String date);
    List<BookingDTO> getBookingsForStudent(String studentId);
    BookingDTO cancelBooking(String bookingId);
    BookingDTO getBookingById(String bookingId);
}