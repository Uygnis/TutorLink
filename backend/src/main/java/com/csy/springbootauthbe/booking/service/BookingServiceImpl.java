package com.csy.springbootauthbe.booking.service;

import com.csy.springbootauthbe.booking.dto.BookingDTO;
import com.csy.springbootauthbe.booking.dto.BookingRequest;
import com.csy.springbootauthbe.booking.dto.RecentBookingResponse;
import com.csy.springbootauthbe.booking.entity.Booking;
import com.csy.springbootauthbe.booking.mapper.BookingMapper;
import com.csy.springbootauthbe.booking.repository.BookingRepository;
import com.csy.springbootauthbe.common.utils.SanitizedLogger;
import com.csy.springbootauthbe.notification.service.NotificationService;
import com.csy.springbootauthbe.user.entity.User;
import com.csy.springbootauthbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;
    private static final SanitizedLogger logger = SanitizedLogger.getLogger(BookingService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Override
    public BookingDTO createBooking(BookingRequest dto) {
        // Check for overlapping booking
        List<Booking> existing = bookingRepository.findByTutorIdAndDate(dto.getTutorId(), dto.getDate());
        boolean overlap = existing.stream().filter(b -> b.getStatus().equals("pending") || b.getStatus().equals("confirmed"))
                .anyMatch(b ->
                (b.getStart().compareTo(dto.getEnd()) < 0) && (dto.getStart().compareTo(b.getEnd()) < 0)
        );

        if (overlap) {
            throw new RuntimeException("Selected slot is already booked.");
        }

        // Create booking
        Booking booking = bookingMapper.toEntity(dto);
        booking.setStatus("pending");
        Booking saved = bookingRepository.save(booking);

        // Notify the tutor
        notificationService.createNotification(
                dto.getTutorId(),                 // recipient is tutor
                "booking_created",                // type
                saved.getId(),                    // booking id
                "A new booking for " + dto.getLessonType() + " has been created."
        );

        return bookingMapper.toDto(saved);
    }

    @Override
    public RecentBookingResponse getRecentPastBookings(String tutorId) {
        String todayStr = LocalDate.now().format(formatter);
        String status = "confirmed";
        long totalCompleted = bookingRepository
                .countByTutorIdAndStatusAndDateBefore(tutorId, status, todayStr);

        List<Booking> recentPastSessions = bookingRepository
                .findTop5ByTutorIdAndStatusAndDateBeforeOrderByDateDesc(
                        tutorId, status, todayStr);

        RecentBookingResponse response = new RecentBookingResponse();
        response.setRecentSessions(recentPastSessions.stream().map(bookingMapper::toDto).toList());
        response.setTotalCount(totalCompleted);

        return response;
    }

    @Override
    public RecentBookingResponse getUpcomingBookings(String tutorId) {
        String todayStr = LocalDate.now().format(formatter);
        List<String> statuses = List.of("confirmed", "pending");

        List<Booking> upcomingSessions = bookingRepository
                .findByTutorIdAndStatusInAndDateGreaterThanEqualOrderByDateAsc(
                        tutorId, statuses, todayStr);

        // limit to 5 upcoming sessions
        List<Booking> next5Sessions = upcomingSessions.stream()
                .limit(5)
                .toList();
        RecentBookingResponse response = new RecentBookingResponse();
        response.setRecentSessions(next5Sessions.stream().map(bookingMapper::toDto).toList());
        response.setTotalCount(upcomingSessions.size());
        return response;
    }



    @Override
    public List<BookingDTO> getBookingsForTutor(String tutorId, String date) {
        return bookingRepository.findByTutorIdAndDate(tutorId, date)
                .stream().map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getBookingsForTutorBetweenDates(String tutorId, String startDate, String endDate) {
        logger.info("Fetching bookings for tutorId={} between {} and {}", tutorId, startDate, endDate);

        List<Booking> bookings = bookingRepository.findBookingsByTutorIdAndDateRange(tutorId, startDate, endDate);

        logger.info("Found {} bookings", bookings.size());
        for (Booking b : bookings) {
            logger.info("Booking: id={}, date={}, status={}", b.getId(), b.getDate(), b.getStatus());
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<BookingDTO> getBookingsForStudent(String studentId) {
        List<Booking> bookings = bookingRepository.findByStudentId(studentId);

        // Sort by date ascending, then start time ascending
        bookings.sort(Comparator
                .comparing(Booking::getDate)                 // assuming getDate() returns java.time.LocalDate or java.util.Date
                .thenComparing(Booking::getStart)           // assuming getStart() returns java.time.LocalTime or String "HH:mm"
        );

        // Get all unique userIds
        Set<String> userIds = bookings.stream()
                .flatMap(b -> Stream.of(b.getStudentId(), b.getTutorId()))
                .collect(Collectors.toSet());

        Map<String, User> usersMap = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return bookings.stream().map(booking -> {
            BookingDTO dto = bookingMapper.toDto(booking);

            User student = usersMap.get(booking.getStudentId());
            User tutor = usersMap.get(booking.getTutorId());

            dto.setStudentName(student.getFirstname() + " " + student.getLastname());
            dto.setTutorName(tutor.getFirstname() + " " + tutor.getLastname());

            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public BookingDTO cancelBooking(String bookingId, String currentUserId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus("cancelled");
        Booking savedBooking = bookingRepository.save(booking);

        // Determine recipient
        String recipientId;
        if (currentUserId.equals(booking.getStudentId())) {
            recipientId = booking.getTutorId(); // student cancelled → notify tutor
        } else {
            recipientId = booking.getStudentId(); // tutor cancelled → notify student
        }

        notificationService.createNotification(
                recipientId,
                "booking_cancelled",
                booking.getId(),
                "Booking for " + booking.getLessonType() + " has been cancelled."
        );

        return bookingMapper.toDto(savedBooking);
    }


    // Only Tutor can accept booking
    @Override
    public BookingDTO acceptBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus("confirmed");
        Booking savedBooking = bookingRepository.save(booking);

        // Trigger notification
        notificationService.createNotification(
                booking.getStudentId(), // student receives notification
                "booking_accepted",
                booking.getId(),
                "Your booking for " + booking.getLessonType() + " has been confirmed!"
        );

        return bookingMapper.toDto(savedBooking);
    }


    @Override
    public BookingDTO getBookingById(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return bookingMapper.toDto(booking);
    }
}
