package com.csy.springbootauthbe.booking.service;

import com.csy.springbootauthbe.booking.dto.BookingDTO;
import com.csy.springbootauthbe.booking.dto.BookingRequest;
import com.csy.springbootauthbe.booking.entity.Booking;
import com.csy.springbootauthbe.booking.mapper.BookingMapper;
import com.csy.springbootauthbe.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDTO createBooking(BookingRequest dto) {
        // Check for overlapping booking
        List<Booking> existing = bookingRepository.findByTutorIdAndDate(dto.getTutorId(), dto.getDate());
        boolean overlap = existing.stream().anyMatch(b ->
                (b.getStart().compareTo(dto.getEnd()) < 0) && (dto.getStart().compareTo(b.getEnd()) < 0)
        );

        if (overlap) {
            throw new RuntimeException("Selected slot is already booked.");
        }

        Booking booking = bookingMapper.toEntity(dto);
        booking.setStatus("pending");
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }

    @Override
    public List<BookingDTO> getBookingsForTutor(String tutorId, String date) {
        return bookingRepository.findByTutorIdAndDate(tutorId, date)
                .stream().map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getBookingsForTutorBetweenDates(String tutorId, String startDate, String endDate) {
        List<Booking> bookings = bookingRepository.findByTutorIdAndDateBetween(tutorId, startDate, endDate);
        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }


    @Override
    public List<BookingDTO> getBookingsForStudent(String studentId) {
        return bookingRepository.findByStudentId(studentId)
                .stream().map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus("cancelled");
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDTO getBookingById(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return bookingMapper.toDto(booking);
    }
}
