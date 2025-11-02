package com.csy.springbootauthbe.booking.service;

import com.csy.springbootauthbe.booking.dto.BookingDTO;
import com.csy.springbootauthbe.booking.dto.BookingRequest;
import com.csy.springbootauthbe.booking.entity.Booking;
import com.csy.springbootauthbe.booking.mapper.BookingMapper;
import com.csy.springbootauthbe.booking.observer.BookingNotificationObserver;
import com.csy.springbootauthbe.booking.repository.BookingRepository;
import com.csy.springbootauthbe.notification.service.NotificationService;
import com.csy.springbootauthbe.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private NotificationService notificationService;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBooking_ShouldCreateBookingSuccessfully() {
        // Arrange
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setTutorId("tutorId");
        bookingRequest.setTutorName("test Tutor");
        bookingRequest.setStudentId("studentId");
        bookingRequest.setStudentName("test Student");
        bookingRequest.setLessonType("test Lesson");
        bookingRequest.setDate("2023-10-01");
        bookingRequest.setStart("10:00");
        bookingRequest.setEnd("11:00");
        bookingRequest.setAmount(BigDecimal.valueOf(100));
        bookingRequest.setLessonType("Math");

        Booking booking = new Booking();
        booking.setId("bookingId");
        booking.setStatus("pending");
        booking.setTutorId(bookingRequest.getTutorId());
        booking.setTutorName(bookingRequest.getTutorName());
        booking.setStudentId(bookingRequest.getStudentId());
        booking.setStudentName(bookingRequest.getStudentName());
        booking.setLessonType(bookingRequest.getLessonType());
        booking.setDate(bookingRequest.getDate());
        booking.setStart(bookingRequest.getStart());
        booking.setEnd(bookingRequest.getEnd());

        when(bookingRepository.findByTutorIdAndDate("tutorId", "2023-10-01")).thenReturn(Collections.emptyList());
        when(bookingRepository.findByStudentIdAndDate("studentId", "2023-10-01")).thenReturn(Collections.emptyList());
        when(bookingMapper.toEntity(bookingRequest)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(new BookingDTO());

        bookingService.addObserver(new BookingNotificationObserver(notificationService));

        // Act
        BookingDTO result = bookingService.createBooking(bookingRequest);

        // Assert
        assertNotNull(result);
        verify(walletService).holdCredits(eq("studentId"), eq(BigDecimal.valueOf(100)), anyString());
        verify(notificationService, times(2)).createNotification(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void cancelBooking_ShouldCancelBookingAndRefundIfApplicable() {
        // Arrange
        String bookingId = "bookingId";
        String currentUserId = "studentId";
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus("pending");
        booking.setAmount(BigDecimal.valueOf(100));
        booking.setStudentId("studentId");
        booking.setTutorId("tutorId");
        booking.setTutorName("test Tutor");
        booking.setStudentName("test Student");
        booking.setLessonType("test Lesson");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(new BookingDTO());

        bookingService.addObserver(new BookingNotificationObserver(notificationService));

        // Act
        BookingDTO result = bookingService.cancelBooking(bookingId, currentUserId);

        // Assert
        assertNotNull(result);
        assertEquals("cancelled", booking.getStatus());
        verify(walletService).refundStudent("studentId", BigDecimal.valueOf(100), bookingId);
        verify(notificationService, times(2)).createNotification(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void acceptBooking_ShouldAcceptPendingBooking() {
        // Arrange
        String bookingId = "bookingId";
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus("pending");
        booking.setAmount(BigDecimal.valueOf(100));
        booking.setStudentId("studentId");
        booking.setTutorId("tutorId");
        booking.setTutorName("test Tutor");
        booking.setStudentName("test Student");
        booking.setLessonType("test Lesson");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(new BookingDTO());

        bookingService.addObserver(new BookingNotificationObserver(notificationService));

        // Act
        BookingDTO result = bookingService.acceptBooking(bookingId);

        // Assert
        assertNotNull(result);
        assertEquals("confirmed", booking.getStatus());
        verify(walletService).releaseToTutor("studentId", "tutorId", BigDecimal.valueOf(100), bookingId);
        verify(notificationService, times(3)).createNotification(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void rejectReschedule_ShouldRejectRescheduleAndRestoreOriginalBooking() {
        // Arrange
        String newBookingId = "newBookingId";
        String originalBookingId = "originalBookingId";

        Booking newBooking = new Booking();
        newBooking.setId(newBookingId);
        newBooking.setStatus("on_hold");
        newBooking.setOriginalBookingId(originalBookingId);
        newBooking.setStudentId("studentId");
        newBooking.setTutorId("tutorId");
        newBooking.setTutorName("test Tutor");
        newBooking.setStudentName("test Student");
        newBooking.setLessonType("test Lesson");

        Booking originalBooking = new Booking();
        originalBooking.setId(originalBookingId);
        originalBooking.setStatus("reschedule_requested");

        when(bookingRepository.findById(newBookingId)).thenReturn(Optional.of(newBooking));
        when(bookingRepository.findById(originalBookingId)).thenReturn(Optional.of(originalBooking));

        BookingDTO originalBookingDTO = new BookingDTO();
        when(bookingMapper.toDto(originalBooking)).thenReturn(originalBookingDTO);

        bookingService.addObserver(new BookingNotificationObserver(notificationService));

        // Act
        BookingDTO result = bookingService.rejectReschedule(newBookingId);

        // Assert
        assertNotNull(result);
        assertEquals(originalBookingDTO, result);

        verify(bookingRepository).findById(newBookingId);
        verify(bookingRepository).findById(originalBookingId);

        verify(bookingRepository).save(originalBooking);
        verify(bookingRepository).save(newBooking);

        verify(notificationService, times(2)).createNotification(anyString(), anyString(), anyString(), anyString());

    }

    @Test
    void rejectReschedule_ShouldThrowException_WhenNewBookingNotFound() {
        // Arrange
        String newBookingId = "nonExistentBookingId";
        when(bookingRepository.findById(newBookingId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookingService.rejectReschedule(newBookingId));
        assertEquals("Booking not found", exception.getMessage());

        verify(bookingRepository).findById(newBookingId);
        verifyNoMoreInteractions(bookingRepository, notificationService);
    }

    @Test
    void rejectReschedule_ShouldThrowException_WhenOriginalBookingNotFound() {
        // Arrange
        String newBookingId = "newBookingId";
        Booking newBooking = new Booking();
        newBooking.setId(newBookingId);
        newBooking.setStatus("on_hold");
        newBooking.setOriginalBookingId("nonExistentOriginalBookingId");

        when(bookingRepository.findById(newBookingId)).thenReturn(Optional.of(newBooking));
        when(bookingRepository.findById(newBooking.getOriginalBookingId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookingService.rejectReschedule(newBookingId));
        assertEquals("Original booking not found", exception.getMessage());

        verify(bookingRepository).findById(newBookingId);
        verify(bookingRepository).findById(newBooking.getOriginalBookingId());
        verifyNoMoreInteractions(bookingRepository, notificationService);
    }

    @Test
    void rejectReschedule_ShouldThrowException_WhenNewBookingStatusIsNotOnHold() {
        // Arrange
        String newBookingId = "newBookingId";
        Booking newBooking = new Booking();
        newBooking.setId(newBookingId);
        newBooking.setStatus("confirmed");

        when(bookingRepository.findById(newBookingId)).thenReturn(Optional.of(newBooking));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookingService.rejectReschedule(newBookingId));
        assertEquals("Only on_hold bookings can be rejected.", exception.getMessage());

        verify(bookingRepository).findById(newBookingId);
        verifyNoMoreInteractions(bookingRepository, notificationService);
    }

    @Test
    void getBookingById_ShouldReturnBookingDto() {
        Booking booking = new Booking();
        booking.setId("B1");
        BookingDTO dto = new BookingDTO();

        when(bookingRepository.findById("B1")).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(dto);

        BookingDTO result = bookingService.getBookingById("B1");
        assertSame(dto, result);
        verify(bookingRepository).findById("B1");
    }

    @Test
    void getBookingById_ShouldThrow_WhenNotFound() {
        when(bookingRepository.findById("X")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.getBookingById("X"));
        assertEquals("Booking not found", ex.getMessage());
    }

    @Test
    void deleteBooking_ShouldMarkCancelledAndReturnDto() {
        Booking booking = new Booking();
        booking.setId("B2");
        booking.setStatus("confirmed");
        when(bookingRepository.findById("B2")).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(new BookingDTO());

        BookingDTO dto = bookingService.deleteBooking("B2");
        assertNotNull(dto);
        assertEquals("cancelled", booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void approveReschedule_ShouldApproveAndReturnDto() {
        Booking newBooking = new Booking();
        newBooking.setId("NB");
        newBooking.setOriginalBookingId("OB");

        Booking oldBooking = new Booking();
        oldBooking.setId("OB");
        oldBooking.setStatus("reschedule_requested");

        when(bookingRepository.findById("NB")).thenReturn(Optional.of(newBooking));
        when(bookingRepository.findById("OB")).thenReturn(Optional.of(oldBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(newBooking);
        when(bookingMapper.toDto(newBooking)).thenReturn(new BookingDTO());

        bookingService.addObserver(new BookingNotificationObserver(notificationService));
        BookingDTO result = bookingService.approveReschedule("NB");
        assertNotNull(result);
        verify(bookingRepository, atLeast(2)).save(any(Booking.class));
        verify(notificationService, atLeastOnce()).createNotification(any(), any(), any(), any());
    }

    @Test
    void getUpcomingBookings_ShouldReturnNextFive() {
        Booking b1 = new Booking(); b1.setId("1");
        Booking b2 = new Booking(); b2.setId("2");
        when(bookingRepository.findByTutorIdAndStatusInAndDateGreaterThanEqualOrderByDateAsc(anyString(), anyList(), anyString()))
                .thenReturn(java.util.List.of(b1, b2));
        when(bookingMapper.toDto(any())).thenReturn(new BookingDTO());

        var resp = bookingService.getUpcomingBookings("T1");
        assertEquals(2, resp.getTotalCount());
        assertEquals(2, resp.getRecentSessions().size());
    }

    @Test
    void getRecentPastBookings_ShouldReturnRecentSessions() {
        Booking b1 = new Booking(); b1.setId("1");
        when(bookingRepository.countByTutorIdAndStatusAndDateBefore(anyString(), anyString(), anyString()))
                .thenReturn(3L);
        when(bookingRepository.findTop5ByTutorIdAndStatusAndDateBeforeOrderByDateDesc(anyString(), anyString(), anyString()))
                .thenReturn(List.of(b1));
        when(bookingMapper.toDto(any())).thenReturn(new BookingDTO());

        var result = bookingService.getRecentPastBookings("T1");
        assertEquals(3, result.getTotalCount());
        assertEquals(1, result.getRecentSessions().size());
    }

    @Test
    void createBooking_ShouldThrow_WhenAmountInvalid() {
        BookingRequest req = new BookingRequest();
        req.setTutorId("T1");
        req.setStudentId("S1");
        req.setDate("2025-11-01");
        req.setAmount(BigDecimal.ZERO);
        when(bookingRepository.findByTutorIdAndDate(anyString(), anyString())).thenReturn(Collections.emptyList());
        when(bookingRepository.findByStudentIdAndDate(anyString(), anyString())).thenReturn(Collections.emptyList());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.createBooking(req));
        assertEquals("Invalid booking amount", ex.getMessage());
    }

    @Test
    void createBooking_ShouldThrow_WhenSlotOverlap() {
        BookingRequest req = new BookingRequest();
        req.setTutorId("T1");
        req.setStudentId("S1");
        req.setDate("2025-11-01");
        req.setStart("10:00");
        req.setEnd("11:00");
        req.setAmount(BigDecimal.TEN);

        Booking existing = new Booking();
        existing.setStart("10:30");
        existing.setEnd("11:30");
        existing.setStatus("pending");

        when(bookingRepository.findByTutorIdAndDate("T1", "2025-11-01")).thenReturn(List.of(existing));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.createBooking(req));
        assertEquals("Selected slot is already booked.", ex.getMessage());
    }

    @Test
    void createBooking_ShouldThrow_WhenConflictWithAnotherTutorSameDay() {
        BookingRequest req = new BookingRequest();
        req.setTutorId("T1");
        req.setStudentId("S1");
        req.setDate("2025-11-01");
        req.setStart("10:00");
        req.setEnd("11:00");
        req.setAmount(BigDecimal.TEN);

        Booking other = new Booking();
        other.setTutorId("T2");
        other.setStart("09:00");
        other.setEnd("10:30");
        other.setStatus("pending");

        when(bookingRepository.findByTutorIdAndDate(any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findByStudentIdAndDate(any(), any())).thenReturn(List.of(other));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.createBooking(req));
        assertEquals("You already have a booking with another tutor on this date.", ex.getMessage());
    }

}