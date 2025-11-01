//package com.csy.springbootauthbe.booking.repository;
//
//import com.csy.springbootauthbe.booking.entity.Booking;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataMongoTest
//class BookingRepositoryTest {
//
//    @Autowired
//    private BookingRepository bookingRepository;
//
//    @BeforeEach
//    void setUp() {
//        bookingRepository.deleteAll();
//
//        Booking booking1 = new Booking();
//        booking1.setId("1");
//        booking1.setTutorId("tutor1");
//        booking1.setDate("2023-10-01");
//        booking1.setStatus("confirmed");
//
//        Booking booking2 = new Booking();
//        booking2.setId("2");
//        booking2.setTutorId("tutor1");
//        booking2.setStudentId("student2");
//        booking2.setDate("2023-10-02");
//        booking2.setStatus("pending");
//
//        Booking booking3 = new Booking();
//        booking3.setId("3");
//        booking3.setTutorId("tutor2");
//        booking3.setStudentId("student1");
//        booking3.setDate("2023-09-30");
//        booking3.setStatus("cancelled");
//
//        bookingRepository.saveAll(Arrays.asList(booking1, booking2, booking3));
//    }
//
//    @Test
//    void findByTutorIdAndDate_ShouldReturnBookingsForTutorAndDate() {
//        List<Booking> bookings = bookingRepository.findByTutorIdAndDate("tutor1", "2023-10-01");
//        assertEquals(1, bookings.size());
//        assertEquals("1", bookings.get(0).getId());
//    }
//
//    @Test
//    void findByStudentId_ShouldReturnBookingsForStudent() {
//        List<Booking> bookings = bookingRepository.findByStudentId("student1");
//        assertEquals(1, bookings.size());
//    }
//
//    @Test
//    void findBookingsByTutorIdAndDateRange_ShouldReturnBookingsWithinDateRange() {
//        List<Booking> bookings = bookingRepository.findBookingsByTutorIdAndDateRange("tutor1", "2023-10-01", "2023-10-02");
//        assertEquals(2, bookings.size());
//    }
//
//    @Test
//    void countByTutorIdAndStatusAndDateBefore_ShouldReturnCountOfBookings() {
//        long count = bookingRepository.countByTutorIdAndStatusAndDateBefore("tutor2", "cancelled", "2023-10-01");
//        assertEquals(1, count);
//    }
//
//    @Test
//    void findTop5ByTutorIdAndStatusAndDateBeforeOrderByDateDesc_ShouldReturnTop5Bookings() {
//        List<Booking> bookings = bookingRepository.findTop5ByTutorIdAndStatusAndDateBeforeOrderByDateDesc("tutor2", "cancelled", "2023-10-01");
//        assertEquals(1, bookings.size());
//        assertEquals("3", bookings.get(0).getId());
//    }
//
//    @Test
//    void findByTutorIdAndStatusInAndDateGreaterThanEqualOrderByDateAsc_ShouldReturnBookings() {
//        List<Booking> bookings = bookingRepository.findByTutorIdAndStatusInAndDateGreaterThanEqualOrderByDateAsc("tutor1", Arrays.asList("confirmed", "pending"), "2023-10-01");
//        assertEquals(2, bookings.size());
//        assertEquals("1", bookings.get(0).getId());
//    }
//
//    @Test
//    void findByStudentIdAndStatusInAndDateBeforeOrderByDateDesc_ShouldReturnBookings() {
//        List<Booking> bookings = bookingRepository.findByStudentIdAndStatusInAndDateBeforeOrderByDateDesc("student1", Arrays.asList("cancelled", "confirmed"), "2023-10-01");
//        assertEquals(1, bookings.size());
//        assertEquals("3", bookings.get(0).getId());
//    }
//
//    @Test
//    void findByStudentIdAndDate_ShouldReturnBookingsForStudentAndDate() {
//        List<Booking> bookings = bookingRepository.findByStudentIdAndDate("student1", "2023-10-01");
//        assertEquals(0, bookings.size());
//    }
//}