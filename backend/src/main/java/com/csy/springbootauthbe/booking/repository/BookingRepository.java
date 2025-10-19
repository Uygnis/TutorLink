package com.csy.springbootauthbe.booking.repository;

import com.csy.springbootauthbe.booking.entity.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByTutorIdAndDate(String tutorId, String date);
    List<Booking> findByStudentId(String studentId);
    @Query("{ 'tutorId': ?0, 'date': { $gte: ?1, $lte: ?2 } }")
    List<Booking> findBookingsByTutorIdAndDateRange(String tutorId, String startDate, String endDate);
}