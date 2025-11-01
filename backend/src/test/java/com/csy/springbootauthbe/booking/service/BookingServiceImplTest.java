package com.csy.springbootauthbe.booking.service;

import com.csy.springbootauthbe.booking.dto.BookingDTO;
import com.csy.springbootauthbe.booking.dto.BookingRequest;
import com.csy.springbootauthbe.booking.entity.Booking;
import com.csy.springbootauthbe.booking.mapper.BookingMapper;
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
        bookingRequest.setStudentId("studentId");
        bookingRequest.setDate("2023-10-01");
        bookingRequest.setStart("10:00");
        bookingRequest.setEnd("11:00");
        bookingRequest.setAmount(BigDecimal.valueOf(100));
        bookingRequest.setLessonType("Math");

        Booking booking = new Booking();
        booking.setId("bookingId");
        booking.setStatus("pending");

        when(bookingRepository.findByTutorIdAndDate("tutorId", "2023-10-01")).thenReturn(Collections.emptyList());
        when(bookingRepository.findByStudentIdAndDate("studentId", "2023-10-01")).thenReturn(Collections.emptyList());
        when(bookingMapper.toEntity(bookingRequest)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(new BookingDTO());

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

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(new BookingDTO());

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

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(new BookingDTO());

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

        Booking originalBooking = new Booking();
        originalBooking.setId(originalBookingId);
        originalBooking.setStatus("reschedule_requested");

        when(bookingRepository.findById(newBookingId)).thenReturn(Optional.of(newBooking));
        when(bookingRepository.findById(originalBookingId)).thenReturn(Optional.of(originalBooking));

        BookingDTO originalBookingDTO = new BookingDTO();
        when(bookingMapper.toDto(originalBooking)).thenReturn(originalBookingDTO);

        // Act
        BookingDTO result = bookingService.rejectReschedule(newBookingId);

        // Assert
        assertNotNull(result);
        assertEquals(originalBookingDTO, result);

        verify(bookingRepository).findById(newBookingId);
        verify(bookingRepository).findById(originalBookingId);

        verify(bookingRepository).save(originalBooking);
        verify(bookingRepository).save(newBooking);

        verify(notificationService).createNotification(
                eq(newBooking.getStudentId()),
                eq("reschedule_rejected"),
                eq(newBooking.getId()),
                anyString()
        );

        verify(notificationService).createNotification(
                eq(newBooking.getTutorId()),
                eq("reschedule_rejected"),
                eq(newBooking.getId()),
                anyString()
        );
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
}