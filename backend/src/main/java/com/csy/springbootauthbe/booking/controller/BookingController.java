package com.csy.springbootauthbe.booking.controller;


import com.csy.springbootauthbe.booking.dto.BookingDTO;
import com.csy.springbootauthbe.booking.dto.BookingRequest;
import com.csy.springbootauthbe.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
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

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<BookingDTO>> getBookingsForStudent(@PathVariable String studentId) {
        List<BookingDTO> bookings = bookingService.getBookingsForStudent(studentId);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable String bookingId) {
        BookingDTO cancelled = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable String bookingId) {
        BookingDTO booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(booking);
    }

}