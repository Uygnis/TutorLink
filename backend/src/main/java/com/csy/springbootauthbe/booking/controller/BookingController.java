package com.csy.springbootauthbe.booking.controller;


import com.csy.springbootauthbe.booking.dto.BookingDTO;
import com.csy.springbootauthbe.booking.dto.BookingRequest;
import com.csy.springbootauthbe.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingRequest dto) {
        BookingDTO booking = bookingService.createBooking(dto);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<BookingDTO>> getBookingsForTutor(
            @PathVariable String tutorId,
            @RequestParam String date) {
        List<BookingDTO> bookings = bookingService.getBookingsForTutor(tutorId, date);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/tutor/range/{tutorId}")
    public ResponseEntity<List<BookingDTO>> getBookingsForTutorInRange(
            @PathVariable String tutorId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        List<BookingDTO> bookings = bookingService.getBookingsForTutorBetweenDates(tutorId, startDate, endDate);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<BookingDTO>> getBookingsForStudent(@PathVariable String studentId) {
        List<BookingDTO> bookings = bookingService.getBookingsForStudent(studentId);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable String bookingId,@PathVariable String currentUserId ) {
        BookingDTO cancelled = bookingService.cancelBooking(bookingId,currentUserId);
        return ResponseEntity.ok(cancelled);
    }

    @PutMapping("/{bookingId}/accept")
    public ResponseEntity<BookingDTO> acceptBooking(@PathVariable String bookingId) {
        BookingDTO cancelled = bookingService.acceptBooking(bookingId);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable String bookingId) {
        BookingDTO booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(booking);
    }

}