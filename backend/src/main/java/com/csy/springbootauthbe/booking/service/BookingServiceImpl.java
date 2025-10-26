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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private static final SanitizedLogger logger = SanitizedLogger.getLogger(BookingServiceImpl.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Override
    public BookingDTO createBooking(BookingRequest dto) {
        // Check for overlapping booking
        List<Booking> existing = bookingRepository.findByTutorIdAndDate(dto.getTutorId(), dto.getDate());
        boolean overlap = existing.stream().filter(b -> b.getStatus().equals("pending") || b.getStatus().equals("confirmed") || b.getStatus().equals("on_hold"))
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
        List<String> statuses = List.of("confirmed", "pending","on_hold");

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

    @Transactional
    public BookingDTO requestReschedule(String bookingId, BookingRequest newSlotRequest) {
        logger.info("Requesting reschedule for bookingId={} with payload: {}", bookingId, newSlotRequest);

        // 1. Fetch current booking
        Booking currentBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.error("Booking not found for id={}", bookingId);
                    return new RuntimeException("Booking not found");
                });
        logger.info("Current booking fetched: {}", currentBooking);

        // 2. Ensure booking is confirmed
        if (!"confirmed".equals(currentBooking.getStatus())) {
            logger.warn("Booking status is not confirmed: {}", currentBooking.getStatus());
            throw new RuntimeException("Only confirmed bookings can be rescheduled.");
        }

        // 3. Check for overlap on new slot
        List<Booking> overlapping = bookingRepository.findByTutorIdAndDate(newSlotRequest.getTutorId(), newSlotRequest.getDate());
        logger.info("Found {} bookings on the same date for tutorId={}", overlapping.size(), newSlotRequest.getTutorId());

        boolean conflict = overlapping.stream()
                .filter(b -> "pending".equals(b.getStatus()) || "confirmed".equals(b.getStatus()) || "on_hold".equals(b.getStatus()))
                .anyMatch(b -> b.getStart().compareTo(newSlotRequest.getEnd()) < 0 &&
                        newSlotRequest.getStart().compareTo(b.getEnd()) < 0);

        if (conflict) {
            logger.warn("Conflict detected for new slot: start={}, end={}", newSlotRequest.getStart(), newSlotRequest.getEnd());
            throw new RuntimeException("Selected slot is already booked.");
        }

        // 4. Update current booking status to RESCHEDULE_REQUESTED
        currentBooking.setStatus("reschedule_requested");
        bookingRepository.save(currentBooking);
        logger.info("Updated current booking to reschedule_requested: {}", currentBooking.getId());

        // 5. Create a new booking in ON_HOLD for the requested slot
        Booking newBooking = bookingMapper.toEntity(newSlotRequest);
        newBooking.setStatus("on_hold");
        newBooking.setOriginalBookingId(currentBooking.getId());
        Booking savedNewBooking = bookingRepository.save(newBooking);
        logger.info("Created new on_hold booking: {}", savedNewBooking.getId());

        // 6. Notify tutor
        notificationService.createNotification(
                currentBooking.getTutorId(),
                "reschedule_requested",
                savedNewBooking.getId(),
                "Student requested reschedule for booking: " + currentBooking.getLessonType()
        );
        logger.info("Notification sent to tutorId={}", currentBooking.getTutorId());

        return bookingMapper.toDto(savedNewBooking);
    }


    @Transactional
    public BookingDTO approveReschedule(String newBookingId) {
        // 1. Fetch new booking
        Booking newBooking = bookingRepository.findById(newBookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // 2. Fetch current booking
        Booking currentBooking = bookingRepository.findById(newBooking.getOriginalBookingId())
                .orElseThrow(() -> new RuntimeException("Original booking not found"));

        // 3. Update current booking → CANCELLED
        currentBooking.setStatus("cancelled");
        bookingRepository.save(currentBooking);

        // 4. Update new booking → CONFIRMED
        newBooking.setStatus("confirmed");
        Booking savedNewBooking = bookingRepository.save(newBooking);

        // 5. Notify student
        notificationService.createNotification(
                newBooking.getStudentId(),
                "reschedule_approved",
                savedNewBooking.getId(),
                "Your rescheduled booking has been confirmed!"
        );

        // 6. Notify tutor (optional)
        notificationService.createNotification(
                newBooking.getTutorId(),
                "reschedule_approved",
                savedNewBooking.getId(),
                "You confirmed the rescheduled booking."
        );

        return bookingMapper.toDto(savedNewBooking);
    }




}
