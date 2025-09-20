package com.csy.springbootauthbe.booking.repository;

import com.csy.springbootauthbe.booking.entity.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByTutorIdAndDate(String tutorId, String date);
    List<Booking> findByStudentId(String studentId);
}